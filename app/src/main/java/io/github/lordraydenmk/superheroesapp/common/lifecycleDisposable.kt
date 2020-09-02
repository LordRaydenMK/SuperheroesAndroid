package io.github.lordraydenmk.superheroesapp.common

import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Holds a [Disposable] and disposes it at the [Lifecycle.Event] specified by [lifecycleEvent]
 *
 * Useful for handling [Disposable] in a [LifecycleOwner] like a Fragment or Activity
 */
class LifecycleDisposable(
    private val disposable: Disposable,
    private val lifecycleEvent: Lifecycle.Event
) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        if (lifecycleEvent == Lifecycle.Event.ON_STOP) disposable.dispose()
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (lifecycleEvent == Lifecycle.Event.ON_DESTROY) disposable.dispose()
        super.onDestroy(owner)
    }
}

@MainThread
fun Disposable.autoDispose(
    lifecycle: Lifecycle,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) = lifecycle.addObserver(LifecycleDisposable(this, lifecycleEvent))