package io.github.lordraydenmk.superheroesapp.common

import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.atomic.AtomicBoolean

class TestViewModel<VS : Any, E : Any> : ViewModelAlgebra<VS, E> {

    private val initialized = AtomicBoolean(false)

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private val _viewState = MutableSharedFlow<VS>(256, 0)

    override val viewState: Flow<VS> = _viewState

    override suspend fun runInitialize(f: suspend () -> Unit) {
        if (initialized.compareAndSet(false, true)) f()
    }

    override suspend fun setState(vs: VS) = _viewState.emit(vs)

    private val _effects = MutableSharedFlow<E>(256, 0)

    override val effects: Flow<E>
        get() = _effects.asSharedFlow()

    override suspend fun runEffect(effect: E) = _effects.emit(effect)
}