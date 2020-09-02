package io.github.lordraydenmk.superheroesapp.common

import io.reactivex.Completable
import io.reactivex.Observable

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
interface Screen<A, VS> {

    val actions: Observable<A>

    fun bind(viewState: VS): Completable
}