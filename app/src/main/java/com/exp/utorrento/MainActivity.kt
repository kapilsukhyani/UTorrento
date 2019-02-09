package com.exp.utorrento

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle

class MainActivity : AppCompatActivity() {

    //very useful for users https://www.best-bittorrent-vpn.com/check-torrent-ip.html
    //https://www.best-bittorrent-vpn.com/downloading-torrents-with-private-internet-access.html
    //https://www.best-bittorrent-vpn.com/safe-torrent-guide.html
    //https://torrentfreak.com/
    //https://www.winxdvd.com/resource/torrent/top-10-torrent-sites-for-music.htm
    //https://lifehacker.com/5411311/bittorrents-future-dht-pex-and-magnet-links-explained

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}
