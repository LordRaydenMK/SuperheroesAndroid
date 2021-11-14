package io.github.lordraydenmk.superheroesapp.utils

import io.github.lordraydenmk.superheroesapp.AppModule

interface TestingModule : AppModule {

    val state: Any

    override var afterBind: (Any) -> Unit
}