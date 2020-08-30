package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.Unrecoverable
import io.github.lordraydenmk.superheroesapp.superheroes.testSuperheroService
import io.kotest.core.spec.style.FunSpec
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SuperheroesKtTest : FunSpec({

    test("superheroes - service with success - converts to domain") {
        val hulkDto = SuperheroDto(42, "Hulk", ThumbnailDto("https://hulk", "jpg"))
        val service = testSuperheroService(listOf(hulkDto))


        val hulk = Superhero(42, "Hulk", "https://hulk.jpg".toHttpUrl())
        service.superheroes()
            .test()
            .awaitCount(1)
            .assertValue(listOf(hulk) to "Marvel rocks!")
    }

    test("superheroes - service with 5XX exception - ServerError") {
        val body = """{}""".toResponseBody()
        val exception = HttpException(Response.error<PaginatedEnvelope<SuperheroDto>>(500, body))
        val service = testSuperheroService(exception)

        service.superheroes()
            .test()
            .assertError {
                it is SuperheroException &&
                        it.error == ServerError(500, "Response.error()")
            }
    }

    test("superheroes - service fails with IOException - NetworkError") {
        val exception = IOException("No Internet!")
        val service = testSuperheroService(exception)

        service.superheroes()
            .test()
            .assertError {
                it is SuperheroException &&
                        it.error == NetworkError(exception)
            }
    }

    test("superheroes - service fails with other error - Unrecoverable") {
        val exception = RuntimeException("Bang!")
        val service = testSuperheroService(exception)

        service.superheroes()
            .test()
            .assertError {
                it is SuperheroException &&
                        it.error == Unrecoverable(exception)
            }
    }
})
