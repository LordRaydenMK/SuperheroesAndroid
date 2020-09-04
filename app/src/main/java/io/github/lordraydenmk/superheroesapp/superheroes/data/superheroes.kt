package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superheroes
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Unrecoverable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

fun SuperheroesService.superheroes(): Single<Superheroes> =
    getSuperheroes()
        .observeOn(Schedulers.computation())
        .onErrorResumeNext(::refineError)
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

/**
 * We treat errors as recoverable (e.g. the server is down, or the internet is slow...) where if
 * the user retries it is likely they will succeed the next time
 *
 * and non-recoverable (e.g. wrong token, bad JSON etc..) which indicate bugs and can't be recovered
 * if the user retries
 *
 * Note: the classification of which errors are recoverable or not will depend on the app.
 * Here the API key is hardcoded so 401 is non-recoverable, in an app where the user logs in with
 * username and password a 401 is recoverable
 */
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