package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import app.cash.turbine.test
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.FakeImageLoader
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl

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
        object : SuperheroesModule, AppModule by AppModule.create(FakeImageLoader, service),
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

        module.program(emptyFlow()).collect()

        viewModel.viewState.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe content
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("FirstLoad - service fails with exception - Loading then Problem") {
        val error = Exception("Unauthorised")
        val service = testSuperheroService(error)

        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(service, viewModel)

        val problem = Problem(IdTextRes(R.string.error_unrecoverable))

        module.program(emptyFlow()).collect()

        viewModel.viewState.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe problem
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("First load then refresh - service fails, then succeeds - Loading, Problem, Loading Content") {
        val error = Exception("Unauthorised")
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

        GlobalScope.launch { module.program(actions).collect() }

        viewModel.viewState.test {
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
            program(flowOf(LoadDetails(42))).collect()
        }

        viewModel.effects.test {
            awaitItem() shouldBe NavigateToDetails(42)
        }
    }
})
