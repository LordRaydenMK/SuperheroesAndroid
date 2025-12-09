package io.github.lordraydenmk.themoviedbapp.movies.data

import io.github.lordraydenmk.themoviedbapp.common.Envelope
import io.github.lordraydenmk.themoviedbapp.movies.MovieException
import io.github.lordraydenmk.themoviedbapp.movies.NetworkError
import io.github.lordraydenmk.themoviedbapp.movies.ServerError
import io.github.lordraydenmk.themoviedbapp.movies.domain.Movie
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieDetails
import io.github.lordraydenmk.themoviedbapp.movies.domain.PopularMovies
import io.github.lordraydenmk.themoviedbapp.movies.testMovieDbService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MovieKtTest {

    @Test
    fun `popularMovies - service with success - converts to domain`() = runTest {
        val hulkDto = MovieDto(
            42,
            "Hulk",
            "Movie overview",
            7.45f,
            "/poster.jpg",
        )
        val service = testMovieDbService(listOf(hulkDto))


        val hulk = Movie(
            42,
            "Hulk",
            "Movie overview",
            7.45f,
            "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl(),
        )
        service.popularMovies() shouldBe PopularMovies(listOf(hulk))
    }

    @Test
    fun `popularMovies - service with 5XX exception - ServerError`() = runTest {
        val body = """{}""".toResponseBody()
        val exception = HttpException(Response.error<Envelope<MovieDto>>(500, body))
        val service = testMovieDbService(exception)

        val e = shouldThrow<MovieException> { service.popularMovies() }
        e.error shouldBe ServerError(500, "Response.error()")
    }

    @Test
    fun `popularMovies - service fails with IOException - NetworkError`() = runTest {
        val exception = IOException("No Internet!")
        val service = testMovieDbService(exception)

        val e = shouldThrow<MovieException> { service.popularMovies() }
        e.error shouldBe NetworkError(exception)
    }

    @Test
    fun `popularMovies - service fails with other error - Unrecoverable`() = runTest {
        val exception = RuntimeException("Bang!")
        val service = testMovieDbService(exception)

        shouldThrow<RuntimeException> { service.popularMovies() }
    }

    @Test
    fun `movieDetails - service with success - converts to domain`() = runTest {
        val hulkDto = MovieDto(
            42,
            "Hulk",
            "Movie overview",
            7.45f,
            "/poster.jpg",
        )
        val service = testMovieDbService(listOf(hulkDto))


        val hulk = Movie(
            42,
            "Hulk",
            "Movie overview",
            7.45f,
            "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl(),
        )
        service.movieDetails(42) shouldBe MovieDetails(hulk)
    }
}
