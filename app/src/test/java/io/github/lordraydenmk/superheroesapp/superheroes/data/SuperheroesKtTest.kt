package io.github.lordraydenmk.superheroesapp.superheroes.data

import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.superheroes.NetworkError
import io.github.lordraydenmk.superheroesapp.superheroes.ServerError
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroException
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Resource
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superhero
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.domain.Superheroes
import io.github.lordraydenmk.superheroesapp.superheroes.testSuperheroService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SuperheroesKtTest : FunSpec({

    test("superheroes - service with success - converts to domain") {
        val hulkDto = SuperheroDto(
            42,
            "Hulk",
            ThumbnailDto("https://hulk", "jpg"),
            ResourceList(1),
            ResourceList(2),
            ResourceList(3),
            ResourceList(4)
        )
        val service = testSuperheroService(listOf(hulkDto))


        val hulk = Superhero(
            42,
            "Hulk",
            "https://hulk.jpg".toHttpUrl(),
            Resource(1),
            Resource(2),
            Resource(3),
            Resource(4)
        )
        service.superheroes() shouldBe Superheroes(listOf(hulk), "Marvel rocks!")
    }

    test("superheroes - service with 5XX exception - ServerError") {
        val body = """{}""".toResponseBody()
        val exception = HttpException(Response.error<PaginatedEnvelope<SuperheroDto>>(500, body))
        val service = testSuperheroService(exception)

        val e = shouldThrow<SuperheroException> { service.superheroes() }
        e.error shouldBe ServerError(500, "Response.error()")
    }

    test("superheroes - service fails with IOException - NetworkError") {
        val exception = IOException("No Internet!")
        val service = testSuperheroService(exception)

        val e = shouldThrow<SuperheroException> { service.superheroes() }
        e.error shouldBe NetworkError(exception)
    }

    test("superheroes - service fails with other error - Unrecoverable") {
        val exception = RuntimeException("Bang!")
        val service = testSuperheroService(exception)

        shouldThrow<RuntimeException> { service.superheroes() }
    }

    test("superheroDetails - service with success - converts to domain") {
        val hulkDto = SuperheroDto(
            42,
            "Hulk",
            ThumbnailDto("https://hulk", "jpg"),
            ResourceList(1),
            ResourceList(2),
            ResourceList(3),
            ResourceList(4)
        )
        val service = testSuperheroService(listOf(hulkDto))


        val hulk = Superhero(
            42,
            "Hulk",
            "https://hulk.jpg".toHttpUrl(),
            Resource(1),
            Resource(2),
            Resource(3),
            Resource(4)
        )
        service.superheroDetails(42) shouldBe SuperheroDetails(hulk, "Marvel rocks!")
    }
})
