package io.github.lordraydenmk.superheroesapp.common

import androidx.lifecycle.ViewModel
import hu.akarnokd.rxjava2.subjects.UnicastWorkSubject
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject

/**
 * A [ViewModelAlgebra] implemented using [ViewModel] from Jetpack
 *
 * It holds a [CompositeDisposable] that is disposed in [onCleared]
 *
 * The state is implemented as [BehaviorSubject] so it caches the last value for it's observers
 * The effects is implemented as [UnicastWorkSubject] so that events are cached until there is a
 * single active subscriber
 *
 * Note: there can be ONLY one subscriber for effects
 */
class JetpackViewModel<VS, E> : ViewModel(), ViewModelAlgebra<VS, E> {

    private val disposables = CompositeDisposable()

    private val _viewState = BehaviorSubject.create<VS>()
    override val viewState: Observable<VS>
        get() = _viewState

    override fun setState(vs: VS): Completable = Completable.fromCallable { _viewState.onNext(vs) }

    private val _viewEffects = UnicastWorkSubject.create<E>()
    override val effects: Observable<E>
        get() = _viewEffects

    override fun runEffect(effect: E): Completable =
        Completable.fromCallable { _viewEffects.onNext(effect) }

    override fun addToDisposable(d: Disposable) {
        disposables += d
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}