package io.github.lordraydenmk.superheroesapp.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A [ViewModelAlgebra] implemented using [ViewModel] from Jetpack
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

    override val effectsF: Flow<E>
        get() = _viewEffects.receiveAsFlow()

    override suspend fun runEffectS(effect: E) = _viewEffects.send(effect)

    override fun onCleared() {
        _viewEffects.close()
        super.onCleared()
    }
}