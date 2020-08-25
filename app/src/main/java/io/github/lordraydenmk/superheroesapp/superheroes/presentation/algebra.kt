package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface SuperheroesVM {

    val viewState: Observable<SuperheroesViewState>

    fun setState(vs: SuperheroesViewState): Completable

    fun addToDisposable(d: Disposable)
}