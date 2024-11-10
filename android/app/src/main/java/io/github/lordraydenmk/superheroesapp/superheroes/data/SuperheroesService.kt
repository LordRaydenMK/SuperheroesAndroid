package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import retrofit2.http.GET
import retrofit2.http.Path

interface SuperheroesService {

    @GET("characters?limit=100")
    suspend fun getSuperheroes(): PaginatedEnvelope<SuperheroDto>

    @GET("characters/{characterId}")
    suspend fun getSuperheroDetails(@Path("characterId") characterId: Long): PaginatedEnvelope<SuperheroDto>
}