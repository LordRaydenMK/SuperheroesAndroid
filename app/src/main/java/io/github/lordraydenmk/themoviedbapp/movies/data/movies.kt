package io.github.lordraydenmk.themoviedbapp.movies.data

import io.github.lordraydenmk.themoviedbapp.movies.MovieException
import io.github.lordraydenmk.themoviedbapp.movies.NetworkError
import io.github.lordraydenmk.themoviedbapp.movies.ServerError
import io.github.lordraydenmk.themoviedbapp.movies.domain.Movie
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieDetails
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import io.github.lordraydenmk.themoviedbapp.movies.domain.PopularMovies
import retrofit2.HttpException
import java.io.IOException

suspend fun TheMovieDbService.popularMovies(): PopularMovies {
    val superheroesDto = runRefineError { getPopularMovies() }
    val superheroes = superheroesDto.results.map { it.toDomain() }
    return PopularMovies(superheroes)
}

suspend fun TheMovieDbService.movieDetails(id: MovieId): MovieDetails {
    val superheroDto = runRefineError { getMovieDetails(id) }
    val superhero = superheroDto.toDomain()
    return MovieDetails(superhero)
}

private fun MovieDto.toDomain(): Movie = Movie.create(
    id = id,
    name = title,
    posterPath = posterPath,
)

private suspend fun <A> runRefineError(f: suspend () -> A): A =
    try {
        f()
    } catch (e: HttpException) {
        throw when (e.code()) {
            in 500..599 -> MovieException(
                ServerError(e.code(), e.message())
            )

            else -> throw IllegalStateException("This should NOT happen", e)
        }
    } catch (e: IOException) {
        throw MovieException(NetworkError(e))
    }