package io.github.lordraydenmk.superheroesapp.common

import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Holds a [Disposable] and disposes it at [Lifecycle.Event.ON_DESTROY]
 *
 * Useful for handling [Disposable] in a [LifecycleOwner] like a Fragment or Activity
 */
class LifecycleDisposable(
    private val disposable: Disposable
) : DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.dispose()
        super.onDestroy(owner)
    }
}

@MainThread
fun Disposable.autoDispose(
    lifecycle: Lifecycle
) = lifecycle.addObserver(LifecycleDisposable(this))