package io.github.lordraydenmk.superheroesapp.common

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Observers effects [effects] between onStart and onStop and executes them in a safe environment
 * using [executeEffect]
 */
class EffectsObserver<E : Any>(
    private val effects: Observable<E>,
    private val executeEffect: (E) -> Unit
) : DefaultLifecycleObserver {

    private var disposable: Disposable? = null

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        disposable = effects.flatMap {
            Observable.fromCallable { executeEffect(it) }
        }.subscribe()
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable!!.dispose()
        super.onStop(owner)
    }
}