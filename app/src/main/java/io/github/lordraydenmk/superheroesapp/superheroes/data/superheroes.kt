package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superheroes
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.Unrecoverable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

fun SuperheroesService.superheroes(): Single<Superheroes> =
    getSuperheroes()
        .onErrorResumeNext(::refineError)
        .observeOn(Schedulers.computation())
        .map { Pair(it.data.results, it.attributionText) }
        .map { (superheroDtos, attributionText) ->
            val superheroes = superheroDtos.map { it.toDomain() }
            Superheroes(superheroes, attributionText)
        }

fun SuperheroesService.superheroDetails(id: SuperheroId): Single<SuperheroDetails> =
    getSuperheroDetails(id)
        .observeOn(Schedulers.computation())
        .onErrorResumeNext(::refineError)
        .map { Pair(it.data.results.first().toDomain(), it.attributionText) }
        .map { (superhero, attributionText) -> SuperheroDetails(superhero, attributionText) }

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

private fun <A> refineError(throwable: Throwable): Single<A> = when (throwable) {
    is HttpException -> {
        val exception = when (throwable.code()) {
            in 500..599 -> SuperheroException(ServerError(throwable.code(), throwable.message()))
            else -> SuperheroException(Unrecoverable(throwable))
        }
        Single.error(exception)
    }
    is IOException -> Single.error(SuperheroException(NetworkError(throwable)))
    else -> Single.error(SuperheroException(Unrecoverable(throwable)))
}