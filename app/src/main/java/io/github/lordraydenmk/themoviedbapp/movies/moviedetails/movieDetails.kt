package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.forkAndForget
import io.github.lordraydenmk.themoviedbapp.common.identity
import io.github.lordraydenmk.themoviedbapp.common.parZip
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.MovieException
import io.github.lordraydenmk.themoviedbapp.movies.NetworkError
import io.github.lordraydenmk.themoviedbapp.movies.ServerError
import io.github.lordraydenmk.themoviedbapp.movies.data.movieDetails
import io.github.lordraydenmk.themoviedbapp.movies.domain.Movie
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MovieDetailsModule : AppModule,
    ViewModelAlgebra<MovieDetailsViewState, MovieDetailsEffect>

suspend fun MovieDetailsModule.program(
    movieId: MovieId,
    actions: Flow<MovieDetailsAction>
): Unit =
    parZip(Dispatchers.Default, { firstLoad(movieId) }, { handleActions(actions) })
    { _, _ -> }

suspend fun MovieDetailsModule.handleActions(actions: Flow<MovieDetailsAction>): Unit =
    actions.map { handleAction(it) }
        .forkAndForget(Dispatchers.Default, scope)

suspend fun MovieDetailsModule.handleAction(action: MovieDetailsAction) = when (action) {
    is Refresh -> loadMovieDetails(action.movieId)
    Up -> runEffect(NavigateUp)
}

suspend fun MovieDetailsModule.firstLoad(movieId: MovieId): Unit {
    runInitialize { loadMovieDetails(movieId) }
}

suspend fun MovieDetailsModule.loadMovieDetails(movieId: MovieId) {
    setState(Loading)
    val viewState = runCatching { movieDetails(movieId) }
        .map { (movie) -> movie.toViewEntity() }
        .map { movie -> Content(movie) }
        .fold(::identity, Throwable::toProblem)
    setState(viewState)
}

private fun Throwable.toProblem(): Problem = when (this) {
    is MovieException -> when (error) {
        is NetworkError -> Problem(ErrorTextRes(R.string.error_recoverable_network))
        is ServerError -> Problem(ErrorTextRes(R.string.error_recoverable_server))
    }

    else -> throw this
}

private fun Movie.toViewEntity(): MovieDetailsViewEntity =
    MovieDetailsViewEntity(
        name = name,
        thumbnail = thumbnail,
    )
