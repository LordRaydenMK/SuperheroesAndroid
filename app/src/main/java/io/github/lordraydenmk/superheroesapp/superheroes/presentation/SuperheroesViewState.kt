package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import androidx.annotation.StringRes
import okhttp3.HttpUrl

data class SuperheroViewEntity(val id: Long, val name: String, val imageUrl: HttpUrl)

sealed class SuperheroesViewState

object Loading : SuperheroesViewState()

data class Content(
    val superheroes: List<SuperheroViewEntity>,
    val copyright: String
) : SuperheroesViewState()

data class Problem(@StringRes val stringId: Int, val recoverable: Boolean) : SuperheroesViewState()
