package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId

sealed class SuperheroDetailsAction
data class FirstLoad(val superheroId: SuperheroId) : SuperheroDetailsAction()
data class Refresh(val superheroId: SuperheroId) : SuperheroDetailsAction()
object Up : SuperheroDetailsAction()