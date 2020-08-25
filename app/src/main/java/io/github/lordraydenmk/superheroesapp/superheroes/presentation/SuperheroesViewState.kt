package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import okhttp3.HttpUrl

data class SuperheroViewEntity(val id: Long, val name: String, val imageUrl: HttpUrl)

sealed class SuperheroesViewState

object Loading : SuperheroesViewState()

data class Content(
    val superheroes: List<SuperheroViewEntity>,
    val copyright: String
) : SuperheroesViewState()

data class Problem(val msg: String) : SuperheroesViewState()
