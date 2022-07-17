package io.github.lordraydenmk.superheroesapp

import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService

private val empty: (Any) -> Unit = { }

/**
 * Contains dependencies with Singleton/App scope
 */
interface AppModule : SuperheroesService {

    val afterBind: (Any) -> Unit
        get() = empty

    companion object {

        internal fun create(service: SuperheroesService): AppModule =
            object : AppModule, SuperheroesService by service {}
    }
}