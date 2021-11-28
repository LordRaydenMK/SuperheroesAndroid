package io.github.lordraydenmk.superheroesapp.utils

import io.github.lordraydenmk.superheroesapp.AppModule
import kotlinx.coroutines.flow.StateFlow

interface TestingModule : AppModule {

    val state: StateFlow<Any>

    override val afterBind: (Any) -> Unit
}