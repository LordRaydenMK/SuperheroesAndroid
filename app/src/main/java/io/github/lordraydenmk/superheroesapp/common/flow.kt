package io.github.lordraydenmk.superheroesapp.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

val unit: Flow<Unit> = flowOf(Unit)

fun <A> Flow<A>.unit(): Flow<Unit> = flatMapMerge { unit }

data class FlowFiber<A>(val join: Flow<A>, val cancel: Job)

fun <A> Flow<A>.fork(
    dispatcher: CoroutineDispatcher,
    scope: CoroutineScope
): Flow<FlowFiber<A>> = flow {
    val shared = MutableSharedFlow<A>(0, 16)

    val job = onEach { shared.emit(it) }
        .flowOn(dispatcher)
        .launchIn(scope)

    emit(FlowFiber(shared.asSharedFlow(), job))
}
