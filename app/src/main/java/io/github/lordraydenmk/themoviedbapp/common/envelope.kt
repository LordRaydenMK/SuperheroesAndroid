package io.github.lordraydenmk.themoviedbapp.common

import kotlinx.serialization.Serializable

@Serializable
data class Envelope<A>(val results: List<A>)