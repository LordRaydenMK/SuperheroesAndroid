package io.github.lordraydenmk.superheroesapp.common.presentation

import io.github.lordraydenmk.superheroesapp.AppModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

fun <D, A : Any, VS : Any, E : Any> D.renderFlow(
    screen: Screen<A, VS>
): Flow<Unit> where D : AppModule, D : ViewModelAlgebra<VS, E> = viewState
    .mapLatest { state -> screen.bind(state).also { afterBind(state) } }