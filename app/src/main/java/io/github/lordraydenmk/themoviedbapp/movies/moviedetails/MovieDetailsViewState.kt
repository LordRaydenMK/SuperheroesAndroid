package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import io.github.lordraydenmk.themoviedbapp.common.TextRes
import okhttp3.HttpUrl

data class MovieDetailsViewEntity(
    val name: String,
    val thumbnail: HttpUrl,
)

sealed class MovieDetailsViewState {

    val title: String
        get() = if (this is Content) movie.name else ""
}

object Loading : MovieDetailsViewState()

data class Content(val movie: MovieDetailsViewEntity) : MovieDetailsViewState()

data class Problem(val stringId: TextRes) : MovieDetailsViewState()