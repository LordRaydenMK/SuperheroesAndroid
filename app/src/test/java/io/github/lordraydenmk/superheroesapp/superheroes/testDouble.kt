package io.github.lordraydenmk.superheroesapp.superheroes

import io.github.lordraydenmk.superheroesapp.common.Envelope
import io.github.lordraydenmk.superheroesapp.common.Paginated
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun testSuperheroService(superheroes: List<SuperheroDto>): SuperheroesService =
    object : SuperheroesService {

        override suspend fun getSuperheroes(): PaginatedEnvelope<SuperheroDto> =
            withContext(Dispatchers.IO) {
                PaginatedEnvelope(200, "Marvel rocks!", Paginated(superheroes))
            }

        override suspend fun getSuperheroDetails(characterId: Long): PaginatedEnvelope<SuperheroDto> =
            withContext(Dispatchers.IO) {
                PaginatedEnvelope(200, "Marvel rocks!", Paginated(superheroes))
            }
    }

fun testSuperheroService(t: Throwable): SuperheroesService = object : SuperheroesService {

    override suspend fun getSuperheroes(): PaginatedEnvelope<SuperheroDto> = throw t

    override suspend fun getSuperheroDetails(characterId: Long): Envelope<Paginated<SuperheroDto>> =
        throw t
}
