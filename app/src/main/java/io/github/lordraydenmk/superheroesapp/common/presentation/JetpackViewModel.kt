package io.github.lordraydenmk.superheroesapp.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
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

    private val _viewState: MutableStateFlow<VS?> = MutableStateFlow(null)
    override val viewStateF: Flow<VS>
        get() = _viewState.filterNotNull()

    override val scope: CoroutineScope
        get() = viewModelScope

    override suspend fun isEmpty(): Boolean = _viewState.value == null

    override suspend fun setStateS(vs: VS): Unit = _viewState.emit(vs)

    private val _viewEffects = Channel<E>(Channel.UNLIMITED)
    override val effects: Observable<E>
        get() = _viewEffects.receiveAsFlow()
            .asObservable()

    override fun runEffect(effect: E): Completable = rxCompletable { _viewEffects.send(effect) }
}