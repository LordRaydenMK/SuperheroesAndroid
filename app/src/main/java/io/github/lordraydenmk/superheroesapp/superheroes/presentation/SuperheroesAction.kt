package io.github.lordraydenmk.superheroesapp.superheroes.presentation

sealed class SuperheroesAction
object FirstLoad : SuperheroesAction()
object Refresh : SuperheroesAction()