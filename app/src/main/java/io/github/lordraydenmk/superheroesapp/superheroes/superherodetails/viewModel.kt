package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.common.presentation.JetpackViewModel
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId

sealed class SuperheroDetailsAction
data class Refresh(val superheroId: SuperheroId) : SuperheroDetailsAction()
object Up : SuperheroDetailsAction()

sealed class SuperheroDetailsEffect
object NavigateUp : SuperheroDetailsEffect()

typealias SuperheroDetailsViewModel = JetpackViewModel<SuperheroDetailsViewState, SuperheroDetailsEffect>