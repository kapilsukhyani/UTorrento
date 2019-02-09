package com.exp.utorrento.extension.bt

import bt.runtime.BtClient
import bt.torrent.TorrentSessionState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

fun BtClient.startAsync(period: Long): Flowable<TorrentSessionState> {
    return Flowable.create<TorrentSessionState>({ emitter ->
        emitter.setCancellable {
            if (isStarted) {
                stop()
            }
        }
        startAsync({ state ->
            emitter.onNext(state)
        }, period)

    }, BackpressureStrategy.ERROR)
}