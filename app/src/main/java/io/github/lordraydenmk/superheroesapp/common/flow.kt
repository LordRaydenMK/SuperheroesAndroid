package io.github.lordraydenmk.superheroesapp.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val unit: Flow<Unit> = flowOf(Unit)

@OptIn(ExperimentalCoroutinesApi::class)
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

suspend fun <A> Flow<A>.forkAndForget(
    dispatcher: CoroutineDispatcher,
    scope: CoroutineScope
): Unit = fork(dispatcher, scope)
    .unit()
    .collect()

// https://github.com/arrow-kt/arrow/blob/a3b0bee5e867632f6a8491a21cbad5f0b4ed4d9f/arrow-libs/fx/arrow-fx-coroutines/src/commonMain/kotlin/arrow/fx/coroutines/ParZip.kt#L76
suspend inline fun <A, B, C> parZip(
    ctx: CoroutineContext = EmptyCoroutineContext,
    crossinline fa: suspend CoroutineScope.() -> A,
    crossinline fb: suspend CoroutineScope.() -> B,
    crossinline f: suspend CoroutineScope.(A, B) -> C
): C = coroutineScope {
    val faa = async(ctx) { fa() }
    val fbb = async(ctx) { fb() }
    val (a, b) = awaitAll(faa, fbb)
    @Suppress("UNCHECKED_CAST")
    f(a as A, b as B)
}

fun <A> Flow<A>.observeIn(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED
): Job =
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            collect()
        }
    }
