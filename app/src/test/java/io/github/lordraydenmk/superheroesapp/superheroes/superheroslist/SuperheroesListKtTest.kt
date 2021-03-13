package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import app.cash.turbine.test
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.rx2.asObservable
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.jupiter.api.Assertions.assertEquals

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
    ): SuperheroesModule = object : SuperheroesModule, AppModule by AppModule.create(service),
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

        viewModel.viewStateF.test {
            assertEquals(Loading, expectItem())
            assertEquals(content, expectItem())
        }
    }

    test("FirstLoad - service fails with exception - Loading then Problem") {
        val error = Exception("Unauthorised")
        val service = testSuperheroService(error)

        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(service, viewModel)

        val problem = Problem(IdTextRes(R.string.error_unrecoverable))

        module.program(emptyFlow()).collect()

        viewModel.viewStateF.test {
            assertEquals(Loading, expectItem())
            assertEquals(problem, expectItem())
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
        module.program(actions).asObservable().subscribe()

        viewModel.viewStateF.test {
            assertEquals(Loading, expectItem())
            assertEquals(Problem::class.java, expectItem()::class.java)

            actions.emit(Refresh)

            assertEquals(Loading, expectItem())
            assertEquals(Content::class.java, expectItem()::class.java)
        }
    }

    test("ShowDetailsAction - NavigateToDetails effect") {
        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(testSuperheroService(emptyList()), viewModel)

        with(module) {
            program(flowOf(LoadDetails(42))).collect()
        }

        viewModel.effects.test()
            .awaitCount(1)
            .assertValue(NavigateToDetails(42))
            .assertNotComplete()
    }
})
