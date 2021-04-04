package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.rx.fork
import io.github.lordraydenmk.superheroesapp.common.rx.logOnError
import io.github.lordraydenmk.superheroesapp.common.rx.unit
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.rxCompletable

interface SuperheroDetailsModule : AppModule,
    ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect>

fun SuperheroDetailsModule.program(
    superheroId: SuperheroId,
    actions: Observable<SuperheroDetailsAction>
): Observable<Unit> =
    actions.flatMap { action ->
        when (action) {
            is Refresh -> loadSuperhero(action.superheroId)
            Up -> runEffect(NavigateUp).toObservable()
        }.fork(Schedulers.computation(), this::addToDisposable)
            .unit()
    }.mergeWith(firstLoad(superheroId))

fun SuperheroDetailsModule.firstLoad(superheroId: SuperheroId): Observable<Unit> =
    isEmpty().flatMap { empty ->
        if (empty) loadSuperhero(superheroId) else Observable.empty()
    }

fun SuperheroDetailsModule.loadSuperhero(superheroId: SuperheroId): Observable<Unit> =
    superheroDetails(superheroId)
        .map { (superhero, attribution) -> superhero.toViewEntity() to attribution }
        .map<SuperheroDetailsViewState> { Content(it.first, it.second) }
        .toObservable()
        .startWith(Loading)
        .logOnError("Error in loadSuperheroes $superheroId")
        .onErrorReturn { t -> t.toProblem() }
        .flatMapCompletable { rxCompletable { setStateS(it) } }
        .toObservable()

private fun Throwable.toProblem(): Problem = when (this) {
    is SuperheroException -> when (error) {
        is NetworkError -> Problem(ErrorTextRes(R.string.error_recoverable_network))
        is ServerError -> Problem(ErrorTextRes(R.string.error_recoverable_server))
        is Unrecoverable -> Problem(IdTextRes(R.string.error_unrecoverable))
    }
    else -> Problem(IdTextRes(R.string.error_unrecoverable))
}

private fun Superhero.toViewEntity(): SuperheroDetailsViewEntity =
    SuperheroDetailsViewEntity(
        name = name,
        thumbnail = thumbnail,
        comics = PlaceholderString(R.string.superhero_details_comics, comics.available),
        stories = PlaceholderString(R.string.superhero_details_stories, stories.available),
        events = PlaceholderString(R.string.superhero_details_events, events.available),
        series = PlaceholderString(R.string.superhero_details_series, series.available),
    )
