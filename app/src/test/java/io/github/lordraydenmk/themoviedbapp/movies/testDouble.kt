package io.github.lordraydenmk.themoviedbapp.movies

import io.github.lordraydenmk.themoviedbapp.common.Envelope
import io.github.lordraydenmk.themoviedbapp.movies.data.MovieDto
import io.github.lordraydenmk.themoviedbapp.movies.data.TheMovieDbService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun testSuperheroService(superheroes: List<MovieDto>): TheMovieDbService =
    object : TheMovieDbService {

        override suspend fun getPopularMovies(): Envelope<MovieDto> =
            withContext(Dispatchers.IO) {
                Envelope(superheroes)
            }

        override suspend fun getMovieDetails(movieId: Long): MovieDto =
            withContext(Dispatchers.IO) {
                superheroes.first { it.id == movieId }
            }
    }

fun testSuperheroService(t: Throwable): TheMovieDbService = object : TheMovieDbService {

    override suspend fun getPopularMovies(): Envelope<MovieDto> = throw t

    override suspend fun getMovieDetails(movieId: Long): MovieDto =
        throw t
}
