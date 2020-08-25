package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.reactivex.Single
import retrofit2.http.GET

interface SuperheroesService {

    @GET("characters")
    fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>>
}