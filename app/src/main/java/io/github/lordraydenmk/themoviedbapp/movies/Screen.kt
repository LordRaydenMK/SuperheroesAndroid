package io.github.lordraydenmk.themoviedbapp.movies

import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId

sealed class Screen {
    data object PopularMovies : Screen()
    data class MovieDetails(val movieId: MovieId) : Screen()
}