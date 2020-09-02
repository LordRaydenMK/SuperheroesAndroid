package io.github.lordraydenmk.superheroesapp.superheroes.domain

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

typealias SuperheroId = Long

data class Resource(val available: Int)

data class Superhero(
    val id: SuperheroId,
    val name: String,
    val thumbnail: HttpUrl,
    val comics: Resource,
    val stories: Resource,
    val events: Resource,
    val series: Resource
) {

    companion object {

        fun create(
            id: Long,
            name: String,
            thumbnailPath: String,
            thumbnailExt: String,
            comicsCount: Int,
            storiesCount: Int,
            eventsCount: Int,
            seriesCount: Int
        ): Superhero {
            val httpsPath = if (thumbnailPath.startsWith("https")) thumbnailPath
            else thumbnailPath.replaceFirst("http", "https")

            val imageUrl = "$httpsPath.$thumbnailExt".toHttpUrl()
            return Superhero(
                id = id,
                name = name,
                thumbnail = imageUrl,
                comics = Resource(comicsCount),
                stories = Resource(storiesCount),
                events = Resource(eventsCount),
                series = Resource(seriesCount)
            )
        }
    }
}

data class Superheroes(val superheroes: List<Superhero>, val attribution: String)

data class SuperheroDetails(val superhero: Superhero, val attribution: String)