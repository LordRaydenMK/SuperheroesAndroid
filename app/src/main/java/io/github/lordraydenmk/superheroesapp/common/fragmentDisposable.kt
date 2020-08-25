package io.github.lordraydenmk.superheroesapp.common

import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

class FragmentDisposable(private val disposable: Disposable) : DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.dispose()
        super.onDestroy(owner)
    }
}

@MainThread
fun Disposable.autoDispose(lifecycle: Lifecycle) =
    lifecycle.addObserver(FragmentDisposable(this))