package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import io.github.lordraydenmk.superheroesapp.common.presentation.JetpackViewModel
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId

sealed class SuperheroesAction
object Refresh : SuperheroesAction()
data class LoadDetails(val id: SuperheroId) : SuperheroesAction()

sealed class SuperheroesEffect
data class NavigateToDetails(val superheroId: SuperheroId) : SuperheroesEffect()

typealias SuperheroesViewModel = JetpackViewModel<SuperheroesViewState, SuperheroesEffect>
