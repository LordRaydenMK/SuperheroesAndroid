package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.TextRes
import okhttp3.HttpUrl

data class SuperheroViewEntity(val id: Long, val name: String, val imageUrl: HttpUrl)

sealed class SuperheroesViewState

object Loading : SuperheroesViewState()

data class Content(
    val superheroes: List<SuperheroViewEntity>,
    val copyright: String
) : SuperheroesViewState()

data class Problem(val stringId: TextRes) : SuperheroesViewState()

val Problem.isRecoverable: Boolean
    get() = stringId is ErrorTextRes
