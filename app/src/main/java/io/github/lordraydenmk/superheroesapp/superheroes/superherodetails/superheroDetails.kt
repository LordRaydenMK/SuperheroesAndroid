package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.common.fork
import io.github.lordraydenmk.superheroesapp.common.identity
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.unit
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

interface SuperheroDetailsModule : AppModule,
    ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect>

fun SuperheroDetailsModule.program(
    superheroId: SuperheroId,
    actions: Flow<SuperheroDetailsAction>
): Flow<Unit> {
    val flow = actions.flatMapMerge { action ->
        when (action) {
            is Refresh -> refreshSuperhero(action.superheroId)
            Up -> flowOf(runEffect(NavigateUp))
        }.fork(Dispatchers.Default, scope)
            .unit()
    }
    return merge(flow, firstLoad(superheroId))
}

fun SuperheroDetailsModule.firstLoad(superheroId: SuperheroId): Flow<Unit> =
    flow { emit(isEmpty()) }
        .flatMapMerge { empty ->
            if (empty) refreshSuperhero(superheroId) else emptyFlow()
        }

fun SuperheroDetailsModule.refreshSuperhero(superheroId: SuperheroId): Flow<Unit> =
    loadSuperhero(superheroId)
        .map { setState(it) }
        .unit()

fun SuperheroDetailsModule.loadSuperhero(superheroId: SuperheroId): Flow<SuperheroDetailsViewState> =
    flow {
        emit(Loading)
        val state = runCatching { superheroDetails(superheroId) }
            .map { (superhero, attribution) -> superhero.toViewEntity() to attribution }
            .map { Content(it.first, it.second) }
            .fold(::identity, Throwable::toProblem)
        emit(state)
    }

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
