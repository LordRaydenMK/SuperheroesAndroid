package io.github.lordraydenmk.themoviedbapp.movies.data

import io.github.lordraydenmk.themoviedbapp.common.Envelope
import retrofit2.http.GET
import retrofit2.http.Path

interface TheMovieDbService {

    @GET("discover/movie")
    suspend fun getPopularMovies(): Envelope<MovieDto>

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Long): MovieDto
}