package io.github.lordraydenmk.superheroesapp.common.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Defines the algebra (set of operations) for a ViewModel
 *
 * It exposes exactly one [Flow] with type [VS] describing the state of the UI
 * It exposes exactly one [Flow] with type [E] for Effects (navigation, show scnackbar etc..)
 *
 * Exposes methods to update the State and Effects
 *
 * Exposes a [CoroutineScope] tied to the lifecycle of this object
 *
 * @see [JetpackViewModel]
 */
interface ViewModelAlgebra<VS : Any, E : Any> {

    val viewState: Flow<VS>

    val scope: CoroutineScope

    suspend fun runInitialize(f: suspend () -> Unit)

    @Suppress("RedundantUnitReturnType")
    suspend fun setState(vs: VS): Unit

    val effects: Flow<E>

    @Suppress("RedundantUnitReturnType")
    suspend fun runEffect(effect: E): Unit
}
