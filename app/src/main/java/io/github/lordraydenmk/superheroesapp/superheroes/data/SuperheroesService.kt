package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface SuperheroesService {

    @GET("characters?limit=100")
    fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>>

    @GET("characters/{characterId}")
    fun getSuperheroDetails(@Path("characterId") characterId: Long): Single<PaginatedEnvelope<SuperheroDto>>
}