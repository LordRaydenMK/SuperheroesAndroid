package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.common.fork
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.rx.logOnError
import io.github.lordraydenmk.superheroesapp.common.unit
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.rx2.rxSingle

interface SuperheroDetailsModule : AppModule,
    ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect>

fun SuperheroDetailsModule.program(
    superheroId: SuperheroId,
    actions: Flow<SuperheroDetailsAction>
): Observable<Unit> {
    val flow = actions.flatMapMerge { action ->
        when (action) {
            is Refresh -> loadSuperhero(action.superheroId).asFlow()
            Up -> flowOf(runEffect(NavigateUp).await())
        }.fork(Dispatchers.Default, scope)
            .unit()
    }
    return merge(flow, firstLoad(superheroId)).asObservable()
}

fun SuperheroDetailsModule.firstLoad(superheroId: SuperheroId): Flow<Unit> =
    flow { emit(isEmpty()) }
        .flatMapMerge { empty ->
            if (empty) loadSuperhero(superheroId).asFlow() else emptyFlow()
        }

fun SuperheroDetailsModule.loadSuperhero(superheroId: SuperheroId): Observable<Unit> =
    rxSingle { superheroDetails(superheroId) }
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
