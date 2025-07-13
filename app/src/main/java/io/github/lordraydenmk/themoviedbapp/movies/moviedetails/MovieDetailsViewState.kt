package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import io.github.lordraydenmk.themoviedbapp.common.TextRes
import okhttp3.HttpUrl

data class MovieDetailsViewEntity(
    val name: String,
    val thumbnail: HttpUrl,
)

sealed class MovieDetailsViewState {

    val title: String
        get() = if (this is Content) superhero.name else ""
}

object Loading : MovieDetailsViewState()

data class Content(
    val superhero: MovieDetailsViewEntity,
    val attribution: String
) : MovieDetailsViewState()

data class Problem(val stringId: TextRes) : MovieDetailsViewState()