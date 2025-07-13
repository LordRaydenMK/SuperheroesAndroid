package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import io.github.lordraydenmk.themoviedbapp.common.presentation.JetpackViewModel
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId

sealed class MovieDetailsAction
data class Refresh(val movieId: MovieId) : MovieDetailsAction()
object Up : MovieDetailsAction()

sealed class MovieDetailsEffect
object NavigateUp : MovieDetailsEffect()

typealias MovieDetailsViewModel = JetpackViewModel<MovieDetailsViewState, MovieDetailsEffect>