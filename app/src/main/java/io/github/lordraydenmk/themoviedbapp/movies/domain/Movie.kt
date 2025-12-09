package io.github.lordraydenmk.themoviedbapp.movies.domain

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

typealias MovieId = Long

data class Movie(
    val id: MovieId,
    val name: String,
    val overview: String,
    val voteAverage: Float,
    val thumbnail: HttpUrl,
) {

    companion object {

        fun create(
            id: Long,
            name: String,
            overview: String,
            voteAverage: Float,
            posterPath: String,
        ): Movie = Movie(
            id = id,
            name = name,
            overview = overview,
            voteAverage = voteAverage,
            thumbnail = "https://image.tmdb.org/t/p/w500$posterPath".toHttpUrl(),
        )
    }
}

data class PopularMovies(val movies: List<Movie>)

data class MovieDetails(val movie: Movie)