package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId

sealed class SuperheroesAction
object FirstLoad : SuperheroesAction()
object Refresh : SuperheroesAction()
data class LoadDetails(val id: SuperheroId) : SuperheroesAction()