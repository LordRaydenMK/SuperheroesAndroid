package io.github.lordraydenmk.superheroesapp.common

import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TestViewModel<VS : Any, E : Any> : ViewModelAlgebra<VS, E> {

    private val _viewState = MutableSharedFlow<VS>(256, 0)
    override val viewStateF: Flow<VS>
        get() = _viewState

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun isEmpty(): Boolean = _viewState.replayCache.isEmpty()

    override suspend fun setStateS(vs: VS) = _viewState.emit(vs)

    private val _effects = MutableSharedFlow<E>(256, 0)

    override val effectsF: Flow<E>
        get() = _effects.asSharedFlow()

    override suspend fun runEffectS(effect: E) = _effects.emit(effect)
}