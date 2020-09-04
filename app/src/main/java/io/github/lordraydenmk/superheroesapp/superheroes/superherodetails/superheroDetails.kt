package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.common.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.fork
import io.github.lordraydenmk.superheroesapp.common.unit
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Unrecoverable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

interface SuperheroDetailsDependencies : AppModule,
    ViewModelAlgebra<SuperheroDetailsViewState, Unit> {

    companion object {

        fun create(
            appModule: AppModule,
            viewModel: ViewModelAlgebra<SuperheroDetailsViewState, Unit>
        ): SuperheroDetailsDependencies =
            object : SuperheroDetailsDependencies,
                AppModule by appModule,
                ViewModelAlgebra<SuperheroDetailsViewState, Unit> by viewModel {}
    }
}

fun SuperheroDetailsDependencies.program(actions: Observable<SuperheroDetailsAction>): Observable<Unit> =
    actions.flatMap { action ->
        when (action) {
            is FirstLoad -> loadSuperheroes(action.superheroId)
            is Refresh -> loadSuperheroes(action.superheroId)
            Up -> runEffect(Unit).toObservable()
        }.fork(Schedulers.computation(), this::addToDisposable)
            .unit()
    }


fun SuperheroDetailsDependencies.loadSuperheroes(superheroId: SuperheroId): Observable<Unit> =
    superheroDetails(superheroId)
        .map { (superhero, attribution) -> superhero.toViewEntity() to attribution }
        .map<SuperheroDetailsViewState> { Content(it.first, it.second) }
        .toObservable()
        .startWith(Loading)
        .onErrorReturn { t -> t.toProblem(superheroId) }
        .flatMapCompletable { setState(it) }
        .toObservable()

private fun Throwable.toProblem(superheroId: SuperheroId): Problem = when (this) {
    is SuperheroException -> when (error) {
        is NetworkError -> Problem(R.string.error_recoverable_network, Refresh(superheroId))
        is ServerError -> Problem(R.string.error_recoverable_server, Refresh(superheroId))
        is Unrecoverable -> Problem(R.string.error_unrecoverable, null)
    }
    else -> Problem(R.string.error_unrecoverable, null)
}

private fun Superhero.toViewEntity(): SuperheroDetailsViewEntity =
    SuperheroDetailsViewEntity(
        name,
        thumbnail,
        PlaceholderString(R.string.superhero_details_comics, comics.available),
        PlaceholderString(R.string.superhero_details_series, series.available),
        PlaceholderString(R.string.superhero_details_events, events.available),
        PlaceholderString(R.string.superhero_details_stories, stories.available),
    )
