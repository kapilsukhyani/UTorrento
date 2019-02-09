package com.exp.utorrento.lib.core

import bt.Bt
import bt.data.file.FileSystemStorage
import bt.dht.DHTConfig
import bt.dht.DHTModule
import bt.magnet.MagnetUriParser
import bt.metainfo.Torrent
import bt.metainfo.TorrentId
import bt.runtime.Config
import bt.torrent.TorrentSessionState
import com.exp.utorrento.extension.bt.startAsync
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

/**
 * [TorrentoLibCore] factory method
 */
@Suppress("FunctionName", "SpellCheckingInspection")
fun TorrentoLibCore(): TorrentoLibCore = TorrentoLibCoreDefaultImpl()

@Suppress("SpellCheckingInspection")
interface TorrentoLibCore {
    /**
     * Downloads a torrent magnet url contents at given location.
     *
     * @param magnetUri, torrent magnet uri.
     * @param storageDir, directory to store downloaded content in.
     * @param period, dictates how far [TorrentDownloadState.Downloading] events should be from each other.
     * @param observeOn, an optional scheduler to indicate to observe stream on
     *
     * @return A stream of [TorrentDownloadState] events.
     */
    fun downloadMagnetContent(
        magnetUri: String,
        storageDir: File,
        period: Long = 2000,
        observeOn: Scheduler = Schedulers.computation()
    ): Flowable<TorrentDownloadState>

    fun downloadTorrentForMagent(
        magnetUri: String,
        observeOn: Scheduler = Schedulers.computation()
    ): Single<TorrentDownloadState.TorrentFileLoaded>
}

@Suppress("SpellCheckingInspection")
internal class TorrentoLibCoreDefaultImpl : TorrentoLibCore {
    private fun defaultConfig(): Config {
        return object : Config() {
            override fun getNumOfHashingThreads(): Int = Runtime.getRuntime().availableProcessors() * 2
        }
    }

    private fun dhtModuleWithDefaultConfig(): DHTModule {
        // enable bootstrapping from public routers
        return DHTModule(object : DHTConfig() {
            override fun shouldUseRouterBootstrap(): Boolean {
                return true
            }
        })
    }

    override fun downloadMagnetContent(
        magnetUri: String,
        storageDir: File,
        period: Long,
        observeOn: Scheduler
    ): Flowable<TorrentDownloadState> {
        return Flowable.fromCallable {
            if (!storageDir.isDirectory) {
                throw IllegalArgumentException("storageDir is not a valid directory")
            }
            // create file system based backend for torrent data
            val storage = FileSystemStorage(storageDir.toPath())
            val uri = MagnetUriParser.parser().parse(magnetUri)
            val torrentFileAvailableEventSource = PublishSubject.create<TorrentDownloadState.TorrentFileLoaded>()

            // create client with a private runtime
            Triple(
                uri.torrentId,
                torrentFileAvailableEventSource,
                Bt.client()
                    .afterFilesChosen {
                        println("Files selected")
                    }
                    .afterTorrentFetched { torrent ->
                        torrentFileAvailableEventSource.onNext(
                            TorrentDownloadState.TorrentFileLoaded(
                                torrent.torrentId,
                                torrent
                            )
                        )
                    }
                    .config(defaultConfig())
                    .storage(storage)
                    .magnet(uri)
                    .autoLoadModules()
                    .module(dhtModuleWithDefaultConfig())
                    .stopWhenDownloaded()
                    .build()
            )
        }
            .subscribeOn(Schedulers.io())
            .flatMap { (torrentId, torrentFileAvailableEventSource, client) ->
                client.startAsync(period)
                    .map<TorrentDownloadState> { sessionState ->
                        if (sessionState.connectedPeers.isEmpty()) {
                            TorrentDownloadState.FindingPeers(torrentId)
                        } else {
                            TorrentDownloadState.Downloading(torrentId, sessionState)
                        }
                    }
                    .mergeWith(torrentFileAvailableEventSource.firstElement().toFlowable())
            }
            .distinctUntilChanged { oldState, newState ->
                oldState is TorrentDownloadState.FindingPeers && oldState == newState
            }
            .startWith(TorrentDownloadState.Validating)
            .observeOn(observeOn)
    }

    override fun downloadTorrentForMagent(
        magnetUri: String,
        observeOn: Scheduler
    ): Single<TorrentDownloadState.TorrentFileLoaded> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

sealed class TorrentDownloadState {
    /**
     * Indicates processing as started and and request going through validation
     */
    object Validating : TorrentDownloadState() {
        override fun toString(): String {
            return "TorrentDownloadState[Validating]"
        }
    }

    /**
     * Indicates that the request has been validated and system is looking for peers to be connected to start download.
     */
    data class FindingPeers(val torrentId: TorrentId) : TorrentDownloadState() {
        override fun toString(): String {
            return "TorrentDownloadState[FindingPeers[torrentId: $torrentId]]"
        }
    }

    /**
     * Indicates torrent file loaded and parsed event.
     *
     * Will be available as a state in stream only when a magnet url is requested
     */
    data class TorrentFileLoaded(val torrentId: TorrentId, val torrent: Torrent) : TorrentDownloadState() {
        override fun toString(): String {
            return "TorrentDownloadState[TorrentFileLoaded[torrentId: $torrentId,\n" +
                    "torrent: [createdBy:  ${torrent.createdBy},\n" +
                    "name:  ${torrent.name},\n" +
                    "noOfFiles:  ${torrent.files.count()},\n" +
                    "source:  ${torrent.source},\n" +
                    "isPrivate:  ${torrent.isPrivate}\n" +
                    "]]]"
        }
    }

    /**
     * Indicates that the downloading has started and includes the states about the download as well
     */
    data class Downloading(val torrentId: TorrentId, val internalState: TorrentSessionState) : TorrentDownloadState() {
        override fun toString(): String {
            return "TorrentDownloadState[Downloading[torrentId: $torrentId,\n" +
                    "Connected Peers: ${internalState.connectedPeers},\n" +
                    "Downloaded: ${internalState.downloaded},\n" +
                    "Pieces total: ${internalState.piecesTotal},\n" +
                    "Pieces complete: ${internalState.piecesComplete},\n" +
                    "Pieces incomplete: ${internalState.piecesIncomplete},\n" +
                    "Pieces skipped: ${internalState.piecesSkipped},\n" +
                    "Pieces not skipped: ${internalState.piecesNotSkipped},\n" +
                    "Pieces remaining: ${internalState.piecesRemaining}\n]]"
        }
    }
}


fun initCore() {
    val targetDirectory = File("/Users/ksukhyani/Downloads")
    val core = TorrentoLibCore()
    val disposable = core.downloadMagnetContent(
        "magnet:?xt=urn:btih:75dc10d7f281351f78a02843d15d737e7fc4d59a&dn=BigSean&tr=udp://thetracker.org:80/announce&tr=udp://tracker.cyberia.is:6969/announce&tr=udp://open.demonii.si:1337/announce&tr=udp://denis.stalker.upeer.me:6969/announce&tr=udp://tracker.port443.xyz:6969/announce&tr=udp://torrentclub.tech:6969/announce&tr=udp://tracker.filemail.com:6969/announce&tr=udp://tracker.filepit.to:6969/announce&tr=udp://tracker.moeking.me:6969/announce&tr=udp://retracker.netbynet.ru:2710/announce&tr=udp://00.syo.mx:53/announce&tr=https://1.tracker.eu.org:443/announce&tr=https://2.tracker.eu.org:443/announce&tr=udp://newtoncity.org:6969/announce&tr=udp://tracker.novg.net:6969/announce&tr=https://3.tracker.eu.org:443/announce&tr=udp://bt.oiyo.tk:6969/announce&tr=udp://retracker.lanta-net.ru:2710/announce&tr=udp://exodus.desync.com:6969/announce&tr=udp://tracker.birkenwald.de:6969/announce&tr=udp://chihaya.toss.li:9696/announce&tr=udp://tracker.skynetcloud.tk:6969/announce&tr=udp://tracker.beeimg.com:6969/announce&tr=http://tracker2.dler.org:80/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://tracker.torrent.eu.org:451/announce&tr=udp://tracker.uw0.xyz:6969/announce&tr=udp://tracker.nyaa.uk:6969/announce&tr=http://vps02.net.orel.ru:80/announce&tr=http://tracker.openzim.org:80/announce&tr=http://tracker.bz:80/announce&tr=udp://retracker.hotplug.ru:2710/announce&tr=udp://tracker.dyn.im:6969/announce&tr=udp://explodie.org:6969/announce&tr=udp://tracker4.itzmx.com:2710/announce&tr=http://t.acg.rip:6699/announce&tr=udp://tracker.dler.org:6969/announce&tr=http://t.nyaatracker.com:80/announce&tr=udp://bt.xxx-tracker.com:2710/announce&tr=udp://ipv4.tracker.harry.lu:80/announce&tr=udp://bigfoot1942.sektori.org:6969/announce&tr=udp://hk1.opentracker.ga:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=http://torrent.nwps.ws:80/announce&tr=http://tracker.gbitt.info:80/announce&tr=https://1337.abcvg.info:443/announce&tr=udp://open.stealth.si:80/announce&tr=http://tracker3.itzmx.com:6961/announce&tr=http://tracker.corpscorp.online:80/announce&tr=udp://tracker.freddit.nl:6969/announce&tr=udp://tracker.iamhansen.xyz:2000/announce&tr=udp://tracker.leechers-paradise.org:6969/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://9.rarbg.com:2710/announce&tr=udp://tracker2.itzmx.com:6961/announce&tr=udp://tracker1.itzmx.com:8080/announce&tr=http://open.trackerlist.xyz:80/announce&tr=https://opentracker.xyz:443/announce&tr=https://tracker.fastdownload.xyz:443/announce&tr=https://t.quic.ws:443/announce&tr=udp://retracker.sevstar.net:2710/announce&tr=http://tracker6.emce.org:12345/announce&tr=udp://tracker.open-tracker.org:1337/announce&tr=http://tracker.torrentyorg.pl:80/announce&tr=udp://ipv6.tracker.harry.lu:80/announce&tr=http://tracker4.emce.org:12345/announce&tr=udp://tracker.swateam.org.uk:2710/announce",
        targetDirectory
    ).subscribe({
        println(it)
    }, {
        println("error $it")
    }, {
        println("completed")
    })
    System.`in`.read()
    disposable.dispose()
}

fun main(args: Array<String>) {
    initCore()
}