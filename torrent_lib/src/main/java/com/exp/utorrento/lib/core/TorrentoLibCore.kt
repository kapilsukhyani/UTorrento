package com.exp.utorrento.lib.core

import bt.Bt
import bt.StandaloneClientBuilder
import bt.data.file.FileSystemStorage
import bt.dht.DHTConfig
import bt.dht.DHTModule
import bt.magnet.MagnetUriParser
import bt.metainfo.Torrent
import bt.metainfo.TorrentFile
import bt.metainfo.TorrentId
import bt.runtime.BtClient
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
     * @param observeOn, an optional scheduler to observe stream on, default is [Schedulers.computation]
     *
     * @return A stream of [TorrentDownloadState] events.
     */
    fun downloadMagnetContent(
        magnetUri: String,
        storageDir: File,
        period: Long = 2000,
        observeOn: Scheduler = Schedulers.computation()
    ): Flowable<TorrentDownloadState>

    /**
     * Downloads torrent file for a give magnet url.
     *
     * @param magnetUri, torrent magnet uri.
     * @param storageDir, directory to store downloaded content in.
     * @param observeOn, an optional scheduler to observe result on, default is [Schedulers.computation]
     *
     * @return A [Single] of [TorrentDownloadState.TorrentFileLoaded]
     */
    fun downloadTorrentForMagent(
        magnetUri: String,
        storageDir: File,
        observeOn: Scheduler = Schedulers.computation()
    ): Single<TorrentDownloadState.TorrentFileLoaded>

    /**
     * Downloads content of provided torrent.
     *
     * @param torrent, [Torrent] to download, this can be downloaded separately given a uri or magnet uri.
     * @param storageDir, directory to store downloaded content in.
     * @param period, dictates how far [TorrentDownloadState.Downloading] events should be from each other.
     * @param observeOn, an optional scheduler to observe stream on, default is [Schedulers.computation]
     *
     * @return A stream of [TorrentDownloadState] events.
     *
     */
    fun downloadTorrent(
        torrent: Torrent,
        storageDir: File,
        period: Long = 2000,
        observeOn: Scheduler = Schedulers.computation()
    ): Flowable<TorrentDownloadState>
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

    private fun getBtClientBuilder(
        config: Config = defaultConfig(),
        dhtModule: DHTModule = dhtModuleWithDefaultConfig(),
        onTorrentFetched: ((Torrent) -> Unit)? = null
    ): StandaloneClientBuilder {
        val onTorrentFetchedCallback = onTorrentFetched ?: {}
        // create client with a private runtime
        return Bt.client()
            .afterFilesChosen {
                //TODO replace with logger
                println("Files selected")
            }
            .afterTorrentFetched(onTorrentFetchedCallback)
            .config(config)
            .autoLoadModules()
            .module(dhtModule)
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
            val client = getBtClientBuilder { torrent ->
                torrentFileAvailableEventSource.onNext(
                    TorrentDownloadState.TorrentFileLoaded(
                        torrent.torrentId,
                        torrent
                    )
                )
            }
                .storage(storage)
                .magnet(uri)
                .stopWhenDownloaded()
                .build()

            Triple(
                uri.torrentId,
                torrentFileAvailableEventSource,
                client
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
        storageDir: File,
        observeOn: Scheduler
    ): Single<TorrentDownloadState.TorrentFileLoaded> {
        //TODO this solution keep the peer tracker alive even when torrent info is downloaded and process doesnot die, need to find a way to download torrent info and complete the process right after that
        return Single.create<TorrentDownloadState.TorrentFileLoaded> { emitter ->
            lateinit var client: BtClient
            val uri = MagnetUriParser.parser().parse(magnetUri)
            // create file system based backend for torrent data
            val storage = FileSystemStorage(storageDir.toPath())

            client = getBtClientBuilder { torrent ->
                if (!emitter.isDisposed) {
                    emitter.onSuccess(
                        TorrentDownloadState.TorrentFileLoaded(
                            torrent.torrentId,
                            torrent
                        )
                    )
                }
                client.stop()
            }
                .storage(storage)
                .magnet(uri)
                .build()

            emitter.setCancellable {
                client.stop()
            }

            client.startAsync()
        }
            .observeOn(observeOn)

    }

    override fun downloadTorrent(
        torrent: Torrent,
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
            val client = getBtClientBuilder()
                .storage(storage)
                .torrent { torrent }
                .stopWhenDownloaded()
                .build()

            Pair(
                torrent.torrentId,
                client
            )
        }
            .subscribeOn(Schedulers.io())
            .flatMap { (torrentId, client) ->
                client.startAsync(period)
                    .map<TorrentDownloadState> { sessionState ->
                        if (sessionState.connectedPeers.isEmpty()) {
                            TorrentDownloadState.FindingPeers(torrentId)
                        } else {
                            TorrentDownloadState.Downloading(torrentId, sessionState)
                        }
                    }
            }
            .distinctUntilChanged { oldState, newState ->
                oldState is TorrentDownloadState.FindingPeers && oldState == newState
            }
            .startWith(TorrentDownloadState.Validating)
            .observeOn(observeOn)
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
        private fun List<TorrentFile>.asString(): String {
            return with(StringBuilder()) {
                append("Files[ \n")
                forEachIndexed { index: Int, torrentFile: TorrentFile ->
                    append("$index: [size: ${torrentFile.size}],\n")
                }
                append("]")
            }
                .toString()
        }

        override fun toString(): String {
            return "TorrentDownloadState[TorrentFileLoaded[torrentId: $torrentId,\n" +
                    "torrent: [createdBy:  ${torrent.createdBy},\n" +
                    "name:  ${torrent.name},\n" +
                    "noOfFiles:  ${torrent.files.count()},\n" +
                    "files:  [${torrent.files.asString()}],\n" +
                    "source:  ${torrent.source},\n" +
                    "isPrivate:  ${torrent.isPrivate}\n" +
                    "size:  ${torrent.size}\n" +
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