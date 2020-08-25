package io.github.lordraydenmk.superheroesapp.superheroes.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.HttpUrl.Companion.toHttpUrl

class SuperheroTest : FunSpec({

    test("create valid superhero") {
        val actual = Superhero.create(42, "Ant Man", "https://antman", "jpg")

        actual shouldBe Superhero(42, "Ant Man", "https://antman.jpg".toHttpUrl())
    }

    test("create - path with http URL - imageUrl with HTTPS url") {
        val actual = Superhero.create(10, "Ant Man", "http://whatever", "jpg")

        actual.thumbnail shouldBe "https://whatever.jpg".toHttpUrl()
    }
})
