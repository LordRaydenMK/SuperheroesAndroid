package io.github.lordraydenmk.themoviedbapp.movies.data

import io.github.lordraydenmk.themoviedbapp.common.Envelope
import io.github.lordraydenmk.themoviedbapp.movies.MovieException
import io.github.lordraydenmk.themoviedbapp.movies.NetworkError
import io.github.lordraydenmk.themoviedbapp.movies.ServerError
import io.github.lordraydenmk.themoviedbapp.movies.domain.Movie
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieDetails
import io.github.lordraydenmk.themoviedbapp.movies.domain.PopularMovies
import io.github.lordraydenmk.themoviedbapp.movies.testSuperheroService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MovieKtTest : FunSpec({

    test("popularMovies - service with success - converts to domain") {
        val hulkDto = MovieDto(
            42,
            "Hulk",
            "/poster.jpg",
        )
        val service = testSuperheroService(listOf(hulkDto))


        val hulk = Movie(
            42,
            "Hulk",
            "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl(),
        )
        service.popularMovies() shouldBe PopularMovies(listOf(hulk))
    }

    test("popularMovies - service with 5XX exception - ServerError") {
        val body = """{}""".toResponseBody()
        val exception = HttpException(Response.error<Envelope<MovieDto>>(500, body))
        val service = testSuperheroService(exception)

        val e = shouldThrow<MovieException> { service.popularMovies() }
        e.error shouldBe ServerError(500, "Response.error()")
    }

    test("popularMovies - service fails with IOException - NetworkError") {
        val exception = IOException("No Internet!")
        val service = testSuperheroService(exception)

        val e = shouldThrow<MovieException> { service.popularMovies() }
        e.error shouldBe NetworkError(exception)
    }

    test("popularMovies - service fails with other error - Unrecoverable") {
        val exception = RuntimeException("Bang!")
        val service = testSuperheroService(exception)

        shouldThrow<RuntimeException> { service.popularMovies() }
    }

    test("movieDetails - service with success - converts to domain") {
        val hulkDto = MovieDto(
            42,
            "Hulk",
            "/poster.jpg",
        )
        val service = testSuperheroService(listOf(hulkDto))


        val hulk = Movie(
            42,
            "Hulk",
            "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl(),
        )
        service.movieDetails(42) shouldBe MovieDetails(hulk)
    }
})
