package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.forkAndForget
import io.github.lordraydenmk.themoviedbapp.common.parZip
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.MovieException
import io.github.lordraydenmk.themoviedbapp.movies.NetworkError
import io.github.lordraydenmk.themoviedbapp.movies.ServerError
import io.github.lordraydenmk.themoviedbapp.movies.data.popularMovies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TheMovieDbModule : AppModule, ViewModelAlgebra<PopularMoviesViewState, MoviesEffect> {
    val actions: Channel<MoviesAction>
}

suspend fun TheMovieDbModule.program(actions: Flow<MoviesAction>): Unit =
    parZip(Dispatchers.Default, { firstLoad() }, { handleActions(actions) })
    { _, _ -> }

suspend fun TheMovieDbModule.handleActions(actions: Flow<MoviesAction>) =
    actions.map { handleAction(it) }
        .forkAndForget(Dispatchers.Default, scope)

suspend fun TheMovieDbModule.handleAction(action: MoviesAction) = when (action) {
    is LoadDetails -> runEffect(NavigateToDetails(action.id))
    Refresh -> loadMovieOne()
}

suspend fun TheMovieDbModule.firstLoad(): Unit =
    runInitialize { loadMovieOne() }

suspend fun TheMovieDbModule.loadMovieOne(): Unit {
    setState(Loading)
    val state = try {
        val popularMovies = popularMovies().movies
            .map { MovieViewEntity(it.id, it.name, it.thumbnail) }
        Content(popularMovies)
    } catch (t: MovieException) {
        mapError(t)
    }
    setState(state)
}

private fun mapError(e: MovieException) = when (e.error) {
    is NetworkError -> Problem(ErrorTextRes(R.string.error_recoverable_network))
    is ServerError -> Problem(ErrorTextRes(R.string.error_recoverable_server))
}