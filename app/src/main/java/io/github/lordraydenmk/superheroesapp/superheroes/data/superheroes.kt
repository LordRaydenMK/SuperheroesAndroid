package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.Unrecoverable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

fun SuperheroesService.superheroes(): Single<Pair<List<Superhero>, String>> =
    getSuperheroes()
        .onErrorResumeNext(::refineError)
        .observeOn(Schedulers.computation())
        .map { Pair(it.data.results, it.attributionText) }
        .map { (superheroDtos, attributionText) ->
            val superhero = superheroDtos.map { superheroDto ->
                with(superheroDto) {
                    Superhero.create(id, name, thumbnail.path, thumbnail.extension)
                }
            }
            superhero to attributionText
        }

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