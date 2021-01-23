package io.github.lordraydenmk.superheroesapp.common

import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.ReplaySubject

class TestViewModel<VS : Any, E : Any> : ViewModelAlgebra<VS, E> {

    private val cd = CompositeDisposable()

    private val _viewState = ReplaySubject.create<VS>()
    override val viewState: Observable<VS>
        get() = _viewState

    override fun isEmpty(): Observable<Boolean> =
        Observable.fromCallable { _viewState.values.isEmpty() }

    override fun setState(vs: VS): Completable =
        Completable.fromCallable { _viewState.onNext(vs) }

    override fun addToDisposable(d: Disposable) {
        cd += d
    }

    private val _effects = ReplaySubject.create<E>()
    override val effects: Observable<E>
        get() = _effects

    override fun runEffect(effect: E): Completable =
        Completable.fromCallable { _effects.onNext(effect) }
}