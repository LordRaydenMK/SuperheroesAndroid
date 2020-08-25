package io.github.lordraydenmk.superheroesapp.superheroes.data

import kotlinx.serialization.Serializable

@Serializable
data class ThumbnailDto(val path: String, val extension: String)

@Serializable
data class SuperheroDto(val id: Long, val name: String, val thumbnail: ThumbnailDto)