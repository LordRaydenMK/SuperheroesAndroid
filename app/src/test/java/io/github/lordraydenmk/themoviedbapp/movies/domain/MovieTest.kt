package io.github.lordraydenmk.themoviedbapp.movies.domain

import io.kotest.matchers.shouldBe
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test

class MovieTest {

    @Test
    fun `create valid movie`() {
        val actual = Movie.create(
            42,
            "Ant Man",
            "Movie overview",
            7.45f,
            "/poster.jpg"
        )

        actual shouldBe Movie(
            42,
            "Ant Man",
            "Movie overview",
            7.45f,
            "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl(),
        )
    }
}
