package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import io.github.lordraydenmk.themoviedbapp.common.presentation.JetpackViewModel
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId

sealed class MoviesAction
object Refresh : MoviesAction()
data class LoadDetails(val id: MovieId) : MoviesAction()

sealed class MoviesEffect
data class NavigateToDetails(val movieId: MovieId) : MoviesEffect()

typealias MoviesViewModel = JetpackViewModel<PopularMoviesViewState, MoviesEffect>
