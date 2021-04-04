package io.github.lordraydenmk.superheroesapp.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxCompletable

/**
 * A [ViewModelAlgebra] implemented using [ViewModel] from Jetpack
 *
 * It holds a [CompositeDisposable] that is disposed in [onCleared]
 *
 * The state is implemented as [MutableStateFlow] so it caches the last value for it's observers
 * The effects is implemented as [Channel] so that events are cached until there is a
 * single active subscriber
 *
 * Note: there can be ONLY one subscriber for effects
 */
class JetpackViewModel<VS : Any, E : Any> : ViewModel(), ViewModelAlgebra<VS, E> {

    private val disposables = CompositeDisposable()

    private val _viewState: MutableStateFlow<VS?> = MutableStateFlow(null)
    override val viewState: Observable<VS>
        get() = _viewState.mapNotNull { it }
            .asObservable()

    override val scope: CoroutineScope
        get() = viewModelScope

    override fun isEmpty(): Observable<Boolean> = _viewState.map { it == null }
        .asObservable()

    override fun setState(vs: VS): Completable =
        Completable.fromAction { _viewState.value = vs }

    private val _viewEffects = Channel<E>(Channel.UNLIMITED)
    override val effects: Observable<E>
        get() = _viewEffects.receiveAsFlow()
            .asObservable()

    override fun runEffect(effect: E): Completable = rxCompletable { _viewEffects.send(effect) }

    override fun addToDisposable(d: Disposable) {
        disposables += d
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}