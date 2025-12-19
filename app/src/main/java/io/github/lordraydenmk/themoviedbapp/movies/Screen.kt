package io.github.lordraydenmk.themoviedbapp.movies

import androidx.navigation3.runtime.NavKey
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object PopularMovies : Screen()

    @Serializable
    data class MovieDetails(val movieId: MovieId) : Screen()
}