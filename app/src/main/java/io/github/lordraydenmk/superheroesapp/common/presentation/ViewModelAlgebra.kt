package io.github.lordraydenmk.superheroesapp.common.presentation

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await

/**
 * Defines the algebra (set of operations) for a ViewModel
 *
 * It exposes exactly one [Observable] with type [VS] describing the state of the UI
 * It exposes exactly one [Observable] with type [E] for Effects (navigation, show scnackbar etc..)
 *
 * Exposes methods to update the State and Effects
 *
 * Exposes a method to add [Disposable] and keep track of it
 *
 * @see [JetpackViewModel]
 */
interface ViewModelAlgebra<VS : Any, E : Any> {

    val viewStateF: Flow<VS>

    val scope: CoroutineScope

    suspend fun isEmpty(): Boolean

    @Suppress("RedundantUnitReturnType")
    suspend fun setStateS(vs: VS): Unit

    val effects: Observable<E>

    val effectsF: Flow<E>
        get() = effects.asFlow()

    fun runEffect(effect: E): Completable

    suspend fun runEffectS(effect: E): Unit = runEffect(effect).await()

    fun addToDisposable(d: Disposable)
}
