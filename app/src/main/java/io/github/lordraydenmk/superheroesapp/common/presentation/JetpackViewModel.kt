package io.github.lordraydenmk.superheroesapp.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicBoolean

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

    private val initialized = AtomicBoolean(false)

    private val _viewState: MutableStateFlow<VS?> = MutableStateFlow(null)
    override val viewState: Flow<VS>
        get() = _viewState.filterNotNull()

    override val scope: CoroutineScope
        get() = viewModelScope

    override suspend fun runInitialize(f: suspend () -> Unit) {
        if (initialized.compareAndSet(false, true)) f()
    }

    override suspend fun setState(vs: VS): Unit = _viewState.emit(vs)

    private val _viewEffects = Channel<E>(Channel.UNLIMITED)

    override val effects: Flow<E>
        get() = _viewEffects.receiveAsFlow()

    override suspend fun runEffect(effect: E) = _viewEffects.send(effect)

    override fun onCleared() {
        _viewEffects.close()
        super.onCleared()
    }
}