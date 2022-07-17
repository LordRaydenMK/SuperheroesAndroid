package io.github.lordraydenmk.superheroesapp.superheroes.data

import kotlinx.serialization.Serializable

@Serializable
data class ThumbnailDto(val path: String, val extension: String)

@Serializable
data class ResourceList(val available: Int)

@Serializable
data class SuperheroDto(
    val id: Long,
    val name: String,
    val thumbnail: ThumbnailDto,
    val comics: ResourceList,
    val stories: ResourceList,
    val events: ResourceList,
    val series: ResourceList
)