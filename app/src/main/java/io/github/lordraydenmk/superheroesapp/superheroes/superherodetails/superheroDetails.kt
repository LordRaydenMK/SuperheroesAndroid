package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.*
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SuperheroDetailsModule : AppModule,
    ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect>

suspend fun SuperheroDetailsModule.program(
    superheroId: SuperheroId,
    actions: Flow<SuperheroDetailsAction>
): Unit =
    parZip(Dispatchers.Default, { firstLoad(superheroId) }, { handleActions(actions) })
    { _, _ -> }

suspend fun SuperheroDetailsModule.handleActions(actions: Flow<SuperheroDetailsAction>): Unit =
    actions.map { handleAction(it) }
        .forkAndForget(Dispatchers.Default, scope)

suspend fun SuperheroDetailsModule.handleAction(action: SuperheroDetailsAction) = when (action) {
    is Refresh -> loadSuperhero(action.superheroId)
    Up -> runEffect(NavigateUp)
}

suspend fun SuperheroDetailsModule.firstLoad(superheroId: SuperheroId): Unit {
    runInitialize { loadSuperhero(superheroId) }
}

suspend fun SuperheroDetailsModule.loadSuperhero(superheroId: SuperheroId) {
    setState(Loading)
    val viewState = runCatching { superheroDetails(superheroId) }
        .map { (superhero, attribution) -> superhero.toViewEntity() to attribution }
        .map { (superhero, attribution) -> Content(superhero, attribution) }
        .fold(::identity, Throwable::toProblem)
    setState(viewState)
}

private fun Throwable.toProblem(): Problem = when (this) {
    is SuperheroException -> when (error) {
        is NetworkError -> Problem(ErrorTextRes(R.string.error_recoverable_network))
        is ServerError -> Problem(ErrorTextRes(R.string.error_recoverable_server))
    }
    else -> throw this
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
