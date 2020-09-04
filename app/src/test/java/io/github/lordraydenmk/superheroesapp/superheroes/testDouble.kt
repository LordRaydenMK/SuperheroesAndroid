package io.github.lordraydenmk.superheroesapp.superheroes

import io.github.lordraydenmk.superheroesapp.common.Paginated
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import io.reactivex.Single

fun testSuperheroService(superheroes: List<SuperheroDto>): SuperheroesService =
    object : SuperheroesService {

        override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> =
            Single.just(PaginatedEnvelope(200, "Marvel rocks!", Paginated(superheroes)))

        override fun getSuperheroDetails(characterId: Long): Single<PaginatedEnvelope<SuperheroDto>> =
            Single.just(PaginatedEnvelope(200, "Marvel rocks!", Paginated(superheroes)))
    }

fun testSuperheroService(t: Throwable): SuperheroesService = object : SuperheroesService {

    override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> = Single.error(t)

    override fun getSuperheroDetails(characterId: Long): Single<PaginatedEnvelope<SuperheroDto>> =
        Single.error(t)
}
