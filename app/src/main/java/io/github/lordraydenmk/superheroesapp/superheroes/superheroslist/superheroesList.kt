package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.fork
import io.github.lordraydenmk.superheroesapp.common.unit
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroes
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

interface SuperheroesDependencies : AppModule, ViewModelAlgebra<SuperheroesViewState, Long>

fun SuperheroesDependencies.program(actions: Observable<SuperheroesAction>): Observable<Unit> =
    actions.flatMap { action ->
        when (action) {
            FirstLoad -> refreshSuperheroes()
            Refresh -> refreshSuperheroes()
            is LoadDetails -> runEffect(action.id).toObservable()
        }.fork(Schedulers.computation(), this::addToDisposable)
            .unit()
    }

fun SuperheroesDependencies.refreshSuperheroes(): Observable<Unit> =
    loadSuperheroes()
        .flatMapCompletable { setState(it) }
        .andThen(unit)


fun SuperheroesDependencies.loadSuperheroes(): Observable<SuperheroesViewState> =
    superheroes()
        .map { (superheroes, attributionText) ->
            superheroes.map {
                SuperheroViewEntity(it.id, it.name, it.thumbnail)
            } to attributionText
        }
        .doOnSuccess { Timber.d("Success, first -> ${it.first.first()}") }
        .map<SuperheroesViewState> { (superheroes, attributionText) ->
            Content(superheroes, attributionText)
        }
        .toObservable()
        .startWith(Loading)
        .doOnError { Timber.e(it, "Error loading characters :/") }
        .onErrorReturn { t ->
            when (t) {
                is SuperheroException -> when (t.error) {
                    is NetworkError -> Problem(R.string.error_recoverable_network, true)
                    is ServerError -> Problem(R.string.error_recoverable_server, true)
                    is Unrecoverable -> Problem(R.string.error_unrecoverable, false)
                }
                else -> Problem(R.string.error_unrecoverable, false)
            }
        }