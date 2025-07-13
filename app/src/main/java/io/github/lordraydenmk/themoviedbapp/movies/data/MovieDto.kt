package io.github.lordraydenmk.themoviedbapp.movies.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Long,
    val title: String,
    @SerialName("poster_path")
    val posterPath: String,
)