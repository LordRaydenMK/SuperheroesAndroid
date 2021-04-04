package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.common.identity
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superheroes
import retrofit2.HttpException
import java.io.IOException

suspend fun SuperheroesService.superheroes(): Superheroes {
    val superheroesDto = runRefineError { getSuperheroes() }
    val superheroes = superheroesDto.data.results.map { it.toDomain() }
    return Superheroes(superheroes, superheroesDto.attributionText)
}

suspend fun SuperheroesService.superheroDetails(id: SuperheroId): SuperheroDetails {
    val superheroDto = runRefineError { getSuperheroDetails(id) }
    val superhero = superheroDto.data.results.first().toDomain()
    return SuperheroDetails(superhero, superheroDto.attributionText)
}

private fun SuperheroDto.toDomain(): Superhero = Superhero.create(
    id = id,
    name = name,
    thumbnailPath = thumbnail.path,
    thumbnailExt = thumbnail.extension,
    comicsCount = comics.available,
    storiesCount = stories.available,
    eventsCount = events.available,
    seriesCount = series.available
)

private suspend fun <A> runRefineError(f: suspend () -> A): A {
    val result = runCatching { f() }
    return result.fold(::identity, handleError())
}

private fun handleError() = { throwable: Throwable ->
    val refined = when (throwable) {
        is HttpException ->
            when (throwable.code()) {
                in 500..599 -> SuperheroException(
                    ServerError(
                        throwable.code(),
                        throwable.message()
                    )
                )
                else -> SuperheroException(Unrecoverable(throwable))
            }
        is IOException -> SuperheroException(NetworkError(throwable))
        else -> SuperheroException(Unrecoverable(throwable))
    }
    throw refined
}