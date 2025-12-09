package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import app.cash.turbine.test
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.common.Envelope
import io.github.lordraydenmk.themoviedbapp.common.TestViewModel
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.data.MovieDto
import io.github.lordraydenmk.themoviedbapp.movies.data.TheMovieDbService
import io.github.lordraydenmk.themoviedbapp.movies.testMovieDbService
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test
import java.io.IOException

class PopularMoviesKtTest {

    fun movieDto(
        id: Long,
        name: String,
        overview: String,
        voteAverage: Float,
        thumbnailPath: String,
    ): MovieDto =
        MovieDto(
            id,
            name,
            overview,
            voteAverage,
            thumbnailPath,
        )

    fun testModule(
        service: TheMovieDbService,
        viewModel: ViewModelAlgebra<PopularMoviesViewState, MoviesEffect>
    ): TheMovieDbModule =
        object : TheMovieDbModule, AppModule by AppModule.create(service),
            ViewModelAlgebra<PopularMoviesViewState, MoviesEffect> by viewModel {}


    @Test
    fun `FirstLoad - service returns a single movie - Loading then Content list `() = runTest {
        val movieDto = movieDto(
            42,
            "Ant Man",
            "Movie overview",
            7.45f,
            "/poster.jpg"
        )
        val service = testMovieDbService(listOf(movieDto))

        val viewModel = TestViewModel<PopularMoviesViewState, MoviesEffect>()
        val module = testModule(service, viewModel)

        val content = Content(
            listOf(
                MovieViewEntity(
                    42,
                    "Ant Man",
                    "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl()
                )
            )
        )

        module.program(emptyFlow())

        viewModel.viewState.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe content
        }
    }

    @Test
    fun `First load then refresh - service fails, then succeeds - Loading, Problem, Loading Content`() =
        runTest {
            val error = IOException("Network issue")
            val movieDto = movieDto(
                42,
                "Ant Man",
                "Movie overview",
                7.45f,
                "/poster.jpg"
            )
            val service = object : TheMovieDbService {
                var i = 0
                override suspend fun getPopularMovies(): Envelope<MovieDto> = when (i++) {
                    0 -> throw error
                    1 -> Envelope(listOf(movieDto))
                    else -> throw IllegalStateException("This should not happen")
                }

                override suspend fun getMovieDetails(movieId: Long): MovieDto =
                    error("This should not be called")
            }

            val viewModel = TestViewModel<PopularMoviesViewState, MoviesEffect>()
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

    @Test
    fun `ShowDetailsAction - NavigateToDetails effect`() = runTest {
        val viewModel = TestViewModel<PopularMoviesViewState, MoviesEffect>()
        val module = testModule(testMovieDbService(emptyList()), viewModel)

        with(module) {
            program(flowOf(LoadDetails(42)))
        }

        viewModel.effects.test {
            awaitItem() shouldBe NavigateToDetails(42)
        }
    }
}
