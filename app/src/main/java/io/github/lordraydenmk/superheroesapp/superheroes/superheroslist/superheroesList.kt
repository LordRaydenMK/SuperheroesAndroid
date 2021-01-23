package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.rx.fork
import io.github.lordraydenmk.superheroesapp.common.rx.logOnError
import io.github.lordraydenmk.superheroesapp.common.rx.unit
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroes
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

interface SuperheroesModule : AppModule, ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect>

fun SuperheroesModule.program(actions: Observable<SuperheroesAction>): Observable<Unit> =
    actions.flatMap { action ->
        when (action) {
            Refresh -> refreshSuperheroes()
            is LoadDetails -> runEffect(NavigateToDetails(action.id)).toObservable()
        }.fork(Schedulers.computation(), this::addToDisposable)
            .unit()
    }.mergeWith(firstLoad())

fun SuperheroesModule.firstLoad(): Observable<Unit> =
    isEmpty().flatMap { empty ->
        if (empty) refreshSuperheroes() else Observable.empty()
    }

fun SuperheroesModule.refreshSuperheroes(): Observable<Unit> =
    loadSuperheroes()
        .flatMapCompletable { setState(it) }
        .andThen(unit)

fun SuperheroesModule.loadSuperheroes(): Observable<SuperheroesViewState> =
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
        .logOnError("Error in loadSuperheroes")
        .onErrorReturn { t ->
            when (t) {
                is SuperheroException -> when (t.error) {
                    is NetworkError -> Problem(ErrorTextRes(R.string.error_recoverable_network))
                    is ServerError -> Problem(ErrorTextRes(R.string.error_recoverable_server))
                    is Unrecoverable -> Problem(IdTextRes(R.string.error_unrecoverable))
                }
                else -> Problem(IdTextRes(R.string.error_unrecoverable))
            }
        }