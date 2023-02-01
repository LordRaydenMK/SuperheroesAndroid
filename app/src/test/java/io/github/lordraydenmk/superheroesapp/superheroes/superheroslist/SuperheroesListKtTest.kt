package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import app.cash.turbine.test
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.common.Paginated
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.common.TestViewModel
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.data.ResourceList
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import io.github.lordraydenmk.superheroesapp.superheroes.data.ThumbnailDto
import io.github.lordraydenmk.superheroesapp.superheroes.testSuperheroService
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

class SuperheroesListKtTest : FunSpec({

    fun superheroDto(
        id: Long,
        name: String,
        thumbnailPath: String,
        thumbnailExt: String
    ): SuperheroDto =
        SuperheroDto(
            id,
            name,
            ThumbnailDto(thumbnailPath, thumbnailExt),
            ResourceList(0),
            ResourceList(0),
            ResourceList(0),
            ResourceList(0)
        )

    fun testModule(
        service: SuperheroesService,
        viewModel: ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect>
    ): SuperheroesModule =
        object : SuperheroesModule, AppModule by AppModule.create(service),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}


    test("FirstLoad - service returns a single superhero - Loading then Content list with 1 item") {
        val superhero = superheroDto(42, "Ant Man", "https://antman", "jpg")
        val service = testSuperheroService(listOf(superhero))

        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(service, viewModel)

        val content = Content(
            listOf(SuperheroViewEntity(42, "Ant Man", "https://antman.jpg".toHttpUrl())),
            "Marvel rocks!"
        )

        module.program(emptyFlow())

        viewModel.viewState.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe content
        }
    }

    test("First load then refresh - service fails, then succeeds - Loading, Problem, Loading Content") {
        val error = IOException("Network issue")
        val superhero = superheroDto(42, "Ant Man", "https://antman", "jpg")
        val service = object : SuperheroesService {
            var i = 0
            override suspend fun getSuperheroes(): PaginatedEnvelope<SuperheroDto> = when (i++) {
                0 -> throw error
                1 -> PaginatedEnvelope(200, "Marvel", Paginated(listOf(superhero)))
                else -> throw IllegalStateException("This should not happen")
            }

            override suspend fun getSuperheroDetails(characterId: Long): PaginatedEnvelope<SuperheroDto> =
                fail("This should not be called")
        }

        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(service, viewModel)

        val actions = MutableSharedFlow<SuperheroesAction>()

        viewModel.viewState.test {
            module.program(actions)

            awaitItem() shouldBe Loading
            awaitItem()::class.java shouldBe Problem::class.java

            actions.emit(Refresh)

            awaitItem() shouldBe Loading
            awaitItem()::class.java shouldBe Content::class.java
        }
    }

    test("ShowDetailsAction - NavigateToDetails effect") {
        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(testSuperheroService(emptyList()), viewModel)

        with(module) {
            program(flowOf(LoadDetails(42)))
        }

        viewModel.effects.test {
            awaitItem() shouldBe NavigateToDetails(42)
        }
    }
})
