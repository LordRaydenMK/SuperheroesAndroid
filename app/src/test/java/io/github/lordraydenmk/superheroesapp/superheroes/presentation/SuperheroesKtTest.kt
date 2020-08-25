package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import io.github.lordraydenmk.superheroesapp.superheroes.data.ThumbnailDto
import io.github.lordraydenmk.superheroesapp.superheroes.testSuperheroService
import io.github.lordraydenmk.superheroesapp.superheroes.testViewModel
import io.kotest.core.spec.style.FunSpec
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.HttpUrl.Companion.toHttpUrl

class SuperheroesKtTest : FunSpec({

    test("FirstLoad - service returns a single superhero - Loading then Content list with 1 item") {
        val superhero = SuperheroDto(42, "Ant Man", ThumbnailDto("https://antman", "jpg"))
        val service = testSuperheroService(listOf(superhero))

        val viewModel = testViewModel()
        val module = object : SuperheroesDependencies, AppModule by AppModule.create(service),
            SuperheroesVM by viewModel {}

        val content = Content(
            listOf(SuperheroViewEntity(42, "Ant Man", "https://antman.jpg".toHttpUrl())),
            "Marvel rocks!"
        )

        module.program(Observable.just(FirstLoad)).subscribe()

        viewModel.viewState.test()
            .awaitCount(2)
            .assertValues(Loading, content)
            .assertNotComplete()
    }

    test("FirstLoad - service fails with exception - Loading then Problem") {
        val error = Exception("Unauthorised")
        val service = object : SuperheroesService {
            override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> =
                Single.error(error)
        }

        val viewModel = testViewModel()
        val module = object : SuperheroesDependencies, AppModule by AppModule.create(service),
            SuperheroesVM by viewModel {}

        val problem = Problem("Unauthorised")

        module.program(Observable.just(FirstLoad)).subscribe()

        viewModel.viewState.test()
            .awaitCount(2)
            .assertValues(Loading, problem)
            .assertNotComplete()
    }
})
