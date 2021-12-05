package io.github.lordraydenmk.superheroesapp.utils

import io.github.lordraydenmk.superheroesapp.AppModule

interface TestingModule : AppModule {

    override val afterBind: (Any) -> Unit

    suspend fun <A : Any> awaitState(clazz: Class<A>, timeoutMs: Long = 3_000)
}