package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.fork
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.unit
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.data.superheroes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

interface SuperheroesModule : AppModule, ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect>

fun SuperheroesModule.program(actions: Flow<SuperheroesAction>): Flow<Unit> {
    val flow = actions.flatMapMerge { action ->
        when (action) {
            Refresh -> refreshSuperheroes()
            is LoadDetails -> flowOf(runEffectS(NavigateToDetails(action.id)))
        }.fork(Dispatchers.Default, scope)
            .unit()
    }

    return merge(firstLoad(), flow)
}

fun SuperheroesModule.firstLoad(): Flow<Unit> =
    flow { emit(isEmpty()) }
        .flatMapMerge { empty ->
            if (empty) refreshSuperheroes() else emptyFlow()
        }

fun SuperheroesModule.refreshSuperheroes(): Flow<Unit> =
    loadSuperheroes()
        .map { setStateS(it) }
        .flowOn(Dispatchers.Default)

fun SuperheroesModule.loadSuperheroes(): Flow<SuperheroesViewState> = flow {
    emit(Loading)
    val state = try {
        val (superheroes, attribution) = superheroes()
        Content(
            superheroes.map { SuperheroViewEntity(it.id, it.name, it.thumbnail) },
            attribution
        )
    } catch (t: Throwable) {
        when (t) {
            is SuperheroException -> when (t.error) {
                is NetworkError -> Problem(ErrorTextRes(R.string.error_recoverable_network))
                is ServerError -> Problem(ErrorTextRes(R.string.error_recoverable_server))
                is Unrecoverable -> Problem(IdTextRes(R.string.error_unrecoverable))
            }
            else -> Problem(IdTextRes(R.string.error_unrecoverable))
        }
    }
    emit(state)
}.flowOn(Dispatchers.Default)