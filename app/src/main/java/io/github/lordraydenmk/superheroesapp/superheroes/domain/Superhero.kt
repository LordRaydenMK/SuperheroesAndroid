package io.github.lordraydenmk.superheroesapp.superheroes.domain

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

typealias SuperheroId = Long

data class Superhero(val id: SuperheroId, val name: String, val thumbnail: HttpUrl) {

    companion object {

        fun create(id: Long, name: String, thumbnailPath: String, thumbnailExt: String): Superhero {
            val httpsPath = if (thumbnailPath.startsWith("https")) thumbnailPath
            else thumbnailPath.replaceFirst("http", "https")

            val imageUrl = "$httpsPath.$thumbnailExt".toHttpUrl()
            return Superhero(id, name, imageUrl)
        }
    }
}