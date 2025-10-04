package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.TextRes
import okhttp3.HttpUrl

data class MovieViewEntity(val id: Long, val name: String, val imageUrl: HttpUrl)

sealed class PopularMoviesViewState

object Loading : PopularMoviesViewState()

data class Content(val movies: List<MovieViewEntity>) : PopularMoviesViewState()

data class Problem(val stringId: TextRes) : PopularMoviesViewState()

val Problem.isRecoverable: Boolean
    get() = stringId is ErrorTextRes
