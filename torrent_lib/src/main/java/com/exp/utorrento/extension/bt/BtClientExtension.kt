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
            if (!emitter.isCancelled) {
                emitter.onNext(state)
            }
        }, period)
            .thenRun {
                if (!emitter.isCancelled) {
                    emitter.onComplete()
                }
            }
            .handle { _, throwable ->
                if (!emitter.isCancelled) {
                    emitter.onError(throwable)
                }
            }
    }, BackpressureStrategy.ERROR)
}