package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import app.cash.turbine.test
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.common.Envelope
import io.github.lordraydenmk.themoviedbapp.common.TestViewModel
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.data.MovieDto
import io.github.lordraydenmk.themoviedbapp.movies.data.TheMovieDbService
import io.github.lordraydenmk.themoviedbapp.movies.testSuperheroService
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

class PopularMoviesKtTest : FunSpec({

    fun movieDto(
        id: Long,
        name: String,
        thumbnailPath: String,
    ): MovieDto =
        MovieDto(
            id,
            name,
            thumbnailPath,
        )

    fun testModule(
        service: TheMovieDbService,
        viewModel: ViewModelAlgebra<SuperheroesViewState, MoviesEffect>
    ): TheMovieDbModule =
        object : TheMovieDbModule, AppModule by AppModule.create(service),
            ViewModelAlgebra<SuperheroesViewState, MoviesEffect> by viewModel {}


    test("FirstLoad - service returns a single movie - Loading then Content list with 1 item") {
        val superhero = movieDto(42, "Ant Man", "/poster.jpg")
        val service = testSuperheroService(listOf(superhero))

        val viewModel = TestViewModel<SuperheroesViewState, MoviesEffect>()
        val module = testModule(service, viewModel)

        val content = Content(
            listOf(SuperheroViewEntity(42, "Ant Man", "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl())),
            ""
        )

        module.program(emptyFlow())

        viewModel.viewState.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe content
        }
    }

    test("First load then refresh - service fails, then succeeds - Loading, Problem, Loading Content") {
        val error = IOException("Network issue")
        val superhero = movieDto(42, "Ant Man", "/poster.jpg")
        val service = object : TheMovieDbService {
            var i = 0
            override suspend fun getPopularMovies(): Envelope<MovieDto> = when (i++) {
                0 -> throw error
                1 -> Envelope(listOf(superhero))
                else -> throw IllegalStateException("This should not happen")
            }

            override suspend fun getMovieDetails(movieId: Long): MovieDto =
                fail("This should not be called")
        }

        val viewModel = TestViewModel<SuperheroesViewState, MoviesEffect>()
        val module = testModule(service, viewModel)

        val actions = MutableSharedFlow<MoviesAction>()

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
        val viewModel = TestViewModel<SuperheroesViewState, MoviesEffect>()
        val module = testModule(testSuperheroService(emptyList()), viewModel)

        with(module) {
            program(flowOf(LoadDetails(42)))
        }

        viewModel.effects.test {
            awaitItem() shouldBe NavigateToDetails(42)
        }
    }
})
