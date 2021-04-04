package io.github.lordraydenmk.superheroesapp.common

import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxCompletable

class TestViewModel<VS : Any, E : Any> : ViewModelAlgebra<VS, E> {

    private val cd = CompositeDisposable()

    private val _viewState = MutableSharedFlow<VS>(256, 0)
    override val viewStateF: Flow<VS>
        get() = _viewState

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun isEmpty(): Boolean = _viewState.replayCache.isEmpty()

    override suspend fun setStateS(vs: VS) = _viewState.emit(vs)

    override fun addToDisposable(d: Disposable) {
        cd += d
    }

    private val _effects = MutableSharedFlow<E>(256, 0)
    override val effects: Observable<E>
        get() = _effects.asObservable()

    override fun runEffect(effect: E): Completable =
        rxCompletable { _effects.emit(effect) }
}