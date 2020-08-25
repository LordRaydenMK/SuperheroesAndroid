package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.common.fork
import io.github.lordraydenmk.superheroesapp.common.unit
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

interface SuperheroesDependencies : AppModule, SuperheroesVM

fun SuperheroesDependencies.program(actions: Observable<SuperheroesAction>): Observable<Unit> =
    actions.flatMap { action ->
        when (action) {
            FirstLoad -> refreshSuperheroes()
            Refresh -> refreshSuperheroes()
        }.fork(Schedulers.computation()) { addToDisposable(it) }
            .unit()
    }

fun SuperheroesDependencies.refreshSuperheroes(): Observable<Unit> =
    loadSuperheroes()
        .flatMapCompletable { setState(it) }
        .andThen(unit)


fun SuperheroesDependencies.loadSuperheroes(): Observable<SuperheroesViewState> =
    getSuperheroes()
        .observeOn(Schedulers.computation())
        .map { Pair(it.data.results, it.attributionText) }
        .map { (superheroes, attributionText) ->
            superheroes.map {
                Superhero.create(it.id, it.name, it.thumbnail.path, it.thumbnail.extension)
            } to attributionText
        }
        .map { (superheroes, attributionText) ->
            superheroes.map {
                SuperheroViewEntity(it.id, it.name, it.thumbnail)
            } to attributionText
        }
        .doOnSuccess { Timber.d("Success, first -> ${it.second}") }
        .map<SuperheroesViewState> { (superheroes, attributionText) ->
            Content(superheroes, attributionText)
        }
        .toObservable()
        .startWith(Loading)
        .doOnError { Timber.e(it, "Error loading characters :/") }
        .onErrorReturn { Problem(it.message ?: "Something went wrong :/") }