package io.github.lordraydenmk.superheroesapp.common.presentation

import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await

/**
 * A container for UI/view related logic
 *
 * A Fragment/Activity delegates the UI logic to this component.
 * It is responsible for findViewById (or a variant of it), setting listeners,
 * updating the View state and transforming the user interaction into Actions
 *
 * This class does NOT do async operations
 *
 * Screen with user actions of type [A] and viewState of type [VS]
 *
 * The Fragment/Activity is not your view (view as in MVVM view, not Android View)
 */
interface Screen<A : Any, VS> {

    val actions: Observable<A>

    val actionsF: Flow<A>
        get() = actions.asFlow()

    fun bind(viewState: VS): Completable

    suspend fun bindS(viewState: VS): Unit = bind(viewState).await()
}