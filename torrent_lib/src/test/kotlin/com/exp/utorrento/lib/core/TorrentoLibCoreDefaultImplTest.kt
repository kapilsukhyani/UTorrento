package com.exp.utorrento.lib.core

import bt.magnet.MagnetUriParser
import bt.metainfo.MetadataService
import bt.metainfo.TorrentFile
import bt.runtime.Config
import bt.torrent.fileselector.SelectionResult
import bt.torrent.fileselector.TorrentFileSelector
import bt.torrent.messaging.MetadataConsumer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

//TODO rectify tests implementation
class TorrentoLibCoreDefaultImplTest {

    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    fun downloadMagnetContent() {
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

    @Test
    fun downloadTorrentInfoForMagnet() {
        val targetDirectory = File("/Users/ksukhyani/Downloads")
        val core = TorrentoLibCore()
        val disposable = core.downloadTorrentInfoForMagent(
            "magnet:?xt=urn:btih:75dc10d7f281351f78a02843d15d737e7fc4d59a&dn=BigSean&tr=udp://thetracker.org:80/announce&tr=udp://tracker.cyberia.is:6969/announce&tr=udp://open.demonii.si:1337/announce&tr=udp://denis.stalker.upeer.me:6969/announce&tr=udp://tracker.port443.xyz:6969/announce&tr=udp://torrentclub.tech:6969/announce&tr=udp://tracker.filemail.com:6969/announce&tr=udp://tracker.filepit.to:6969/announce&tr=udp://tracker.moeking.me:6969/announce&tr=udp://retracker.netbynet.ru:2710/announce&tr=udp://00.syo.mx:53/announce&tr=https://1.tracker.eu.org:443/announce&tr=https://2.tracker.eu.org:443/announce&tr=udp://newtoncity.org:6969/announce&tr=udp://tracker.novg.net:6969/announce&tr=https://3.tracker.eu.org:443/announce&tr=udp://bt.oiyo.tk:6969/announce&tr=udp://retracker.lanta-net.ru:2710/announce&tr=udp://exodus.desync.com:6969/announce&tr=udp://tracker.birkenwald.de:6969/announce&tr=udp://chihaya.toss.li:9696/announce&tr=udp://tracker.skynetcloud.tk:6969/announce&tr=udp://tracker.beeimg.com:6969/announce&tr=http://tracker2.dler.org:80/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://tracker.torrent.eu.org:451/announce&tr=udp://tracker.uw0.xyz:6969/announce&tr=udp://tracker.nyaa.uk:6969/announce&tr=http://vps02.net.orel.ru:80/announce&tr=http://tracker.openzim.org:80/announce&tr=http://tracker.bz:80/announce&tr=udp://retracker.hotplug.ru:2710/announce&tr=udp://tracker.dyn.im:6969/announce&tr=udp://explodie.org:6969/announce&tr=udp://tracker4.itzmx.com:2710/announce&tr=http://t.acg.rip:6699/announce&tr=udp://tracker.dler.org:6969/announce&tr=http://t.nyaatracker.com:80/announce&tr=udp://bt.xxx-tracker.com:2710/announce&tr=udp://ipv4.tracker.harry.lu:80/announce&tr=udp://bigfoot1942.sektori.org:6969/announce&tr=udp://hk1.opentracker.ga:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=http://torrent.nwps.ws:80/announce&tr=http://tracker.gbitt.info:80/announce&tr=https://1337.abcvg.info:443/announce&tr=udp://open.stealth.si:80/announce&tr=http://tracker3.itzmx.com:6961/announce&tr=http://tracker.corpscorp.online:80/announce&tr=udp://tracker.freddit.nl:6969/announce&tr=udp://tracker.iamhansen.xyz:2000/announce&tr=udp://tracker.leechers-paradise.org:6969/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://9.rarbg.com:2710/announce&tr=udp://tracker2.itzmx.com:6961/announce&tr=udp://tracker1.itzmx.com:8080/announce&tr=http://open.trackerlist.xyz:80/announce&tr=https://opentracker.xyz:443/announce&tr=https://tracker.fastdownload.xyz:443/announce&tr=https://t.quic.ws:443/announce&tr=udp://retracker.sevstar.net:2710/announce&tr=http://tracker6.emce.org:12345/announce&tr=udp://tracker.open-tracker.org:1337/announce&tr=http://tracker.torrentyorg.pl:80/announce&tr=udp://ipv6.tracker.harry.lu:80/announce&tr=http://tracker4.emce.org:12345/announce&tr=udp://tracker.swateam.org.uk:2710/announce",
            targetDirectory
        )
            .subscribe({
                println(it)
            }, {
                println("error $it")
            })
        System.`in`.read()
        disposable.dispose()
    }

    @Test
    fun downloadTorrentForMagnetAndThenDownloadTorrentExample() {
        val targetDirectory = File("/Users/ksukhyani/Downloads")
        val core = TorrentoLibCore()
        val disposable = core.downloadTorrentInfoForMagent(
            "magnet:?xt=urn:btih:75dc10d7f281351f78a02843d15d737e7fc4d59a&dn=BigSean&tr=udp://thetracker.org:80/announce&tr=udp://tracker.cyberia.is:6969/announce&tr=udp://open.demonii.si:1337/announce&tr=udp://denis.stalker.upeer.me:6969/announce&tr=udp://tracker.port443.xyz:6969/announce&tr=udp://torrentclub.tech:6969/announce&tr=udp://tracker.filemail.com:6969/announce&tr=udp://tracker.filepit.to:6969/announce&tr=udp://tracker.moeking.me:6969/announce&tr=udp://retracker.netbynet.ru:2710/announce&tr=udp://00.syo.mx:53/announce&tr=https://1.tracker.eu.org:443/announce&tr=https://2.tracker.eu.org:443/announce&tr=udp://newtoncity.org:6969/announce&tr=udp://tracker.novg.net:6969/announce&tr=https://3.tracker.eu.org:443/announce&tr=udp://bt.oiyo.tk:6969/announce&tr=udp://retracker.lanta-net.ru:2710/announce&tr=udp://exodus.desync.com:6969/announce&tr=udp://tracker.birkenwald.de:6969/announce&tr=udp://chihaya.toss.li:9696/announce&tr=udp://tracker.skynetcloud.tk:6969/announce&tr=udp://tracker.beeimg.com:6969/announce&tr=http://tracker2.dler.org:80/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://tracker.torrent.eu.org:451/announce&tr=udp://tracker.uw0.xyz:6969/announce&tr=udp://tracker.nyaa.uk:6969/announce&tr=http://vps02.net.orel.ru:80/announce&tr=http://tracker.openzim.org:80/announce&tr=http://tracker.bz:80/announce&tr=udp://retracker.hotplug.ru:2710/announce&tr=udp://tracker.dyn.im:6969/announce&tr=udp://explodie.org:6969/announce&tr=udp://tracker4.itzmx.com:2710/announce&tr=http://t.acg.rip:6699/announce&tr=udp://tracker.dler.org:6969/announce&tr=http://t.nyaatracker.com:80/announce&tr=udp://bt.xxx-tracker.com:2710/announce&tr=udp://ipv4.tracker.harry.lu:80/announce&tr=udp://bigfoot1942.sektori.org:6969/announce&tr=udp://hk1.opentracker.ga:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=http://torrent.nwps.ws:80/announce&tr=http://tracker.gbitt.info:80/announce&tr=https://1337.abcvg.info:443/announce&tr=udp://open.stealth.si:80/announce&tr=http://tracker3.itzmx.com:6961/announce&tr=http://tracker.corpscorp.online:80/announce&tr=udp://tracker.freddit.nl:6969/announce&tr=udp://tracker.iamhansen.xyz:2000/announce&tr=udp://tracker.leechers-paradise.org:6969/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://9.rarbg.com:2710/announce&tr=udp://tracker2.itzmx.com:6961/announce&tr=udp://tracker1.itzmx.com:8080/announce&tr=http://open.trackerlist.xyz:80/announce&tr=https://opentracker.xyz:443/announce&tr=https://tracker.fastdownload.xyz:443/announce&tr=https://t.quic.ws:443/announce&tr=udp://retracker.sevstar.net:2710/announce&tr=http://tracker6.emce.org:12345/announce&tr=udp://tracker.open-tracker.org:1337/announce&tr=http://tracker.torrentyorg.pl:80/announce&tr=udp://ipv6.tracker.harry.lu:80/announce&tr=http://tracker4.emce.org:12345/announce&tr=udp://tracker.swateam.org.uk:2710/announce",
            targetDirectory
        )
            .flatMapPublisher {
                println("Torrent downloaded, continuing with download")
                core.downloadTorrentContent(it.torrent, targetDirectory)
            }
            .subscribe({
                println(it)
            }, {
                println("error $it")
            }, {
                println("completed")
            })

        System.`in`.read()
        disposable.dispose()
    }

    @Test
    fun downloadTorrentForMagnetWithoutClientExample() {
        //this solution isn't working, need to figure out what is missing
        val magnet = MagnetUriParser.parser()
            .parse("magnet:?xt=urn:btih:75dc10d7f281351f78a02843d15d737e7fc4d59a&dn=BigSean&tr=udp://thetracker.org:80/announce&tr=udp://tracker.cyberia.is:6969/announce&tr=udp://open.demonii.si:1337/announce&tr=udp://denis.stalker.upeer.me:6969/announce&tr=udp://tracker.port443.xyz:6969/announce&tr=udp://torrentclub.tech:6969/announce&tr=udp://tracker.filemail.com:6969/announce&tr=udp://tracker.filepit.to:6969/announce&tr=udp://tracker.moeking.me:6969/announce&tr=udp://retracker.netbynet.ru:2710/announce&tr=udp://00.syo.mx:53/announce&tr=https://1.tracker.eu.org:443/announce&tr=https://2.tracker.eu.org:443/announce&tr=udp://newtoncity.org:6969/announce&tr=udp://tracker.novg.net:6969/announce&tr=https://3.tracker.eu.org:443/announce&tr=udp://bt.oiyo.tk:6969/announce&tr=udp://retracker.lanta-net.ru:2710/announce&tr=udp://exodus.desync.com:6969/announce&tr=udp://tracker.birkenwald.de:6969/announce&tr=udp://chihaya.toss.li:9696/announce&tr=udp://tracker.skynetcloud.tk:6969/announce&tr=udp://tracker.beeimg.com:6969/announce&tr=http://tracker2.dler.org:80/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://tracker.torrent.eu.org:451/announce&tr=udp://tracker.uw0.xyz:6969/announce&tr=udp://tracker.nyaa.uk:6969/announce&tr=http://vps02.net.orel.ru:80/announce&tr=http://tracker.openzim.org:80/announce&tr=http://tracker.bz:80/announce&tr=udp://retracker.hotplug.ru:2710/announce&tr=udp://tracker.dyn.im:6969/announce&tr=udp://explodie.org:6969/announce&tr=udp://tracker4.itzmx.com:2710/announce&tr=http://t.acg.rip:6699/announce&tr=udp://tracker.dler.org:6969/announce&tr=http://t.nyaatracker.com:80/announce&tr=udp://bt.xxx-tracker.com:2710/announce&tr=udp://ipv4.tracker.harry.lu:80/announce&tr=udp://bigfoot1942.sektori.org:6969/announce&tr=udp://hk1.opentracker.ga:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=http://torrent.nwps.ws:80/announce&tr=http://tracker.gbitt.info:80/announce&tr=https://1337.abcvg.info:443/announce&tr=udp://open.stealth.si:80/announce&tr=http://tracker3.itzmx.com:6961/announce&tr=http://tracker.corpscorp.online:80/announce&tr=udp://tracker.freddit.nl:6969/announce&tr=udp://tracker.iamhansen.xyz:2000/announce&tr=udp://tracker.leechers-paradise.org:6969/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://9.rarbg.com:2710/announce&tr=udp://tracker2.itzmx.com:6961/announce&tr=udp://tracker1.itzmx.com:8080/announce&tr=http://open.trackerlist.xyz:80/announce&tr=https://opentracker.xyz:443/announce&tr=https://tracker.fastdownload.xyz:443/announce&tr=https://t.quic.ws:443/announce&tr=udp://retracker.sevstar.net:2710/announce&tr=http://tracker6.emce.org:12345/announce&tr=udp://tracker.open-tracker.org:1337/announce&tr=http://tracker.torrentyorg.pl:80/announce&tr=udp://ipv6.tracker.harry.lu:80/announce&tr=http://tracker4.emce.org:12345/announce&tr=udp://tracker.swateam.org.uk:2710/announce")
        val service = MetadataService()
        val metadataConsumer = MetadataConsumer(service, magnet.torrentId, Config())
        val torrent = metadataConsumer.waitForTorrent()
        print(TorrentDownloadState.TorrentFileLoaded(torrent.torrentId, torrent))
    }

    @Test
    fun downloadTorrentContentWithFileSelectorExample() {
        val targetDirectory = File("/Users/ksukhyani/Downloads")
        val core = TorrentoLibCore()
        val disposable = core.downloadMagnetContent(
            "magnet:?xt=urn:btih:75dc10d7f281351f78a02843d15d737e7fc4d59a&dn=BigSean&tr=udp://thetracker.org:80/announce&tr=udp://tracker.cyberia.is:6969/announce&tr=udp://open.demonii.si:1337/announce&tr=udp://denis.stalker.upeer.me:6969/announce&tr=udp://tracker.port443.xyz:6969/announce&tr=udp://torrentclub.tech:6969/announce&tr=udp://tracker.filemail.com:6969/announce&tr=udp://tracker.filepit.to:6969/announce&tr=udp://tracker.moeking.me:6969/announce&tr=udp://retracker.netbynet.ru:2710/announce&tr=udp://00.syo.mx:53/announce&tr=https://1.tracker.eu.org:443/announce&tr=https://2.tracker.eu.org:443/announce&tr=udp://newtoncity.org:6969/announce&tr=udp://tracker.novg.net:6969/announce&tr=https://3.tracker.eu.org:443/announce&tr=udp://bt.oiyo.tk:6969/announce&tr=udp://retracker.lanta-net.ru:2710/announce&tr=udp://exodus.desync.com:6969/announce&tr=udp://tracker.birkenwald.de:6969/announce&tr=udp://chihaya.toss.li:9696/announce&tr=udp://tracker.skynetcloud.tk:6969/announce&tr=udp://tracker.beeimg.com:6969/announce&tr=http://tracker2.dler.org:80/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://tracker.torrent.eu.org:451/announce&tr=udp://tracker.uw0.xyz:6969/announce&tr=udp://tracker.nyaa.uk:6969/announce&tr=http://vps02.net.orel.ru:80/announce&tr=http://tracker.openzim.org:80/announce&tr=http://tracker.bz:80/announce&tr=udp://retracker.hotplug.ru:2710/announce&tr=udp://tracker.dyn.im:6969/announce&tr=udp://explodie.org:6969/announce&tr=udp://tracker4.itzmx.com:2710/announce&tr=http://t.acg.rip:6699/announce&tr=udp://tracker.dler.org:6969/announce&tr=http://t.nyaatracker.com:80/announce&tr=udp://bt.xxx-tracker.com:2710/announce&tr=udp://ipv4.tracker.harry.lu:80/announce&tr=udp://bigfoot1942.sektori.org:6969/announce&tr=udp://hk1.opentracker.ga:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=http://torrent.nwps.ws:80/announce&tr=http://tracker.gbitt.info:80/announce&tr=https://1337.abcvg.info:443/announce&tr=udp://open.stealth.si:80/announce&tr=http://tracker3.itzmx.com:6961/announce&tr=http://tracker.corpscorp.online:80/announce&tr=udp://tracker.freddit.nl:6969/announce&tr=udp://tracker.iamhansen.xyz:2000/announce&tr=udp://tracker.leechers-paradise.org:6969/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://9.rarbg.com:2710/announce&tr=udp://tracker2.itzmx.com:6961/announce&tr=udp://tracker1.itzmx.com:8080/announce&tr=http://open.trackerlist.xyz:80/announce&tr=https://opentracker.xyz:443/announce&tr=https://tracker.fastdownload.xyz:443/announce&tr=https://t.quic.ws:443/announce&tr=udp://retracker.sevstar.net:2710/announce&tr=http://tracker6.emce.org:12345/announce&tr=udp://tracker.open-tracker.org:1337/announce&tr=http://tracker.torrentyorg.pl:80/announce&tr=udp://ipv6.tracker.harry.lu:80/announce&tr=http://tracker4.emce.org:12345/announce&tr=udp://tracker.swateam.org.uk:2710/announce",
            targetDirectory,
            fileSelector = object : TorrentFileSelector() {
                override fun select(file: TorrentFile): SelectionResult {
                    // file greater than 10 mb will be ignored
                    if (file.size > 10 * 1024 * 1024) {
                        return SelectionResult.skip()
                    }
                    return SelectionResult.select().build()
                }

            }
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

    @Test
    fun downloadTorrentContentSequentiallyExample() {
        val targetDirectory = File("/Users/ksukhyani/Downloads")
        val core = TorrentoLibCore()
        val disposable = core.downloadMagnetContent(
            "magnet:?xt=urn:btih:75dc10d7f281351f78a02843d15d737e7fc4d59a&dn=BigSean&tr=udp://thetracker.org:80/announce&tr=udp://tracker.cyberia.is:6969/announce&tr=udp://open.demonii.si:1337/announce&tr=udp://denis.stalker.upeer.me:6969/announce&tr=udp://tracker.port443.xyz:6969/announce&tr=udp://torrentclub.tech:6969/announce&tr=udp://tracker.filemail.com:6969/announce&tr=udp://tracker.filepit.to:6969/announce&tr=udp://tracker.moeking.me:6969/announce&tr=udp://retracker.netbynet.ru:2710/announce&tr=udp://00.syo.mx:53/announce&tr=https://1.tracker.eu.org:443/announce&tr=https://2.tracker.eu.org:443/announce&tr=udp://newtoncity.org:6969/announce&tr=udp://tracker.novg.net:6969/announce&tr=https://3.tracker.eu.org:443/announce&tr=udp://bt.oiyo.tk:6969/announce&tr=udp://retracker.lanta-net.ru:2710/announce&tr=udp://exodus.desync.com:6969/announce&tr=udp://tracker.birkenwald.de:6969/announce&tr=udp://chihaya.toss.li:9696/announce&tr=udp://tracker.skynetcloud.tk:6969/announce&tr=udp://tracker.beeimg.com:6969/announce&tr=http://tracker2.dler.org:80/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://tracker.torrent.eu.org:451/announce&tr=udp://tracker.uw0.xyz:6969/announce&tr=udp://tracker.nyaa.uk:6969/announce&tr=http://vps02.net.orel.ru:80/announce&tr=http://tracker.openzim.org:80/announce&tr=http://tracker.bz:80/announce&tr=udp://retracker.hotplug.ru:2710/announce&tr=udp://tracker.dyn.im:6969/announce&tr=udp://explodie.org:6969/announce&tr=udp://tracker4.itzmx.com:2710/announce&tr=http://t.acg.rip:6699/announce&tr=udp://tracker.dler.org:6969/announce&tr=http://t.nyaatracker.com:80/announce&tr=udp://bt.xxx-tracker.com:2710/announce&tr=udp://ipv4.tracker.harry.lu:80/announce&tr=udp://bigfoot1942.sektori.org:6969/announce&tr=udp://hk1.opentracker.ga:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=http://torrent.nwps.ws:80/announce&tr=http://tracker.gbitt.info:80/announce&tr=https://1337.abcvg.info:443/announce&tr=udp://open.stealth.si:80/announce&tr=http://tracker3.itzmx.com:6961/announce&tr=http://tracker.corpscorp.online:80/announce&tr=udp://tracker.freddit.nl:6969/announce&tr=udp://tracker.iamhansen.xyz:2000/announce&tr=udp://tracker.leechers-paradise.org:6969/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://9.rarbg.com:2710/announce&tr=udp://tracker2.itzmx.com:6961/announce&tr=udp://tracker1.itzmx.com:8080/announce&tr=http://open.trackerlist.xyz:80/announce&tr=https://opentracker.xyz:443/announce&tr=https://tracker.fastdownload.xyz:443/announce&tr=https://t.quic.ws:443/announce&tr=udp://retracker.sevstar.net:2710/announce&tr=http://tracker6.emce.org:12345/announce&tr=udp://tracker.open-tracker.org:1337/announce&tr=http://tracker.torrentyorg.pl:80/announce&tr=udp://ipv6.tracker.harry.lu:80/announce&tr=http://tracker4.emce.org:12345/announce&tr=udp://tracker.swateam.org.uk:2710/announce",
            targetDirectory,
            downloadDataSequentially = true
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
}