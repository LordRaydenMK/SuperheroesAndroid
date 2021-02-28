package io.github.lordraydenmk.superheroesapp.common.rx

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

/**
 * Observers effects [effects] between onStart and onStop and executes them in a safe environment
 * using [executeEffect]
 */
class EffectsObserver<E : Any>(
    private val effects: Observable<E>,
    private val executeEffect: (E) -> Unit
) : DefaultLifecycleObserver {

    private val effectObs: (E) -> Observable<Unit> = { value: E ->
        Observable.fromCallable { executeEffect(value) }
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    private val stopRelay: PublishSubject<Any> = PublishSubject.create()

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        effects.flatMap(effectObs)
            .takeUntil(stopRelay)
            .subscribe()
    }

    override fun onStop(owner: LifecycleOwner) {
        stopRelay.onNext(Unit)
        super.onStop(owner)
    }
}