package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import app.cash.turbine.test
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.TestViewModel
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.data.MovieDto
import io.github.lordraydenmk.themoviedbapp.movies.data.TheMovieDbService
import io.github.lordraydenmk.themoviedbapp.movies.testSuperheroService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

class MovieDetailsKtTest : FunSpec({

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

    fun module(
        service: TheMovieDbService,
        viewModelAlgebra: ViewModelAlgebra<MovieDetailsViewState, MovieDetailsEffect>
    ) = object : MovieDetailsModule, AppModule by AppModule.create(service),
        ViewModelAlgebra<MovieDetailsViewState, MovieDetailsEffect> by viewModelAlgebra {}


    test("FirstLoad - service with success - Movie") {
        val hulkDto = movieDto(42, "Hulk", "/poster.jpg")
        val service = testSuperheroService(listOf(hulkDto))

        val viewModel = TestViewModel<MovieDetailsViewState, MovieDetailsEffect>()
        val module = module(service, viewModel)

        with(module) {
            program(42, emptyFlow())

            val hulk = MovieDetailsViewEntity(
                "Hulk",
                "https://image.tmdb.org/t/p/w500/poster.jpg".toHttpUrl(),
            )
            viewModel.viewState.test {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Content(hulk, "")
            }
        }
    }

    test("FirstLoad - service with error - Problem") {
        val error = IOException("Bang")
        val service = testSuperheroService(error)

        val viewModel = TestViewModel<MovieDetailsViewState, MovieDetailsEffect>()
        val module = module(service, viewModel)

        with(module) {
            program(42, emptyFlow())

            val expectedProblem = Problem(ErrorTextRes(R.string.error_recoverable_network))
            viewModel.viewState.test {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe expectedProblem
            }
        }
    }

    test("Action Up - NavigateUp Effect") {
        val hulkDto = movieDto(42, "Hulk", "/poster.jpg")
        val viewModel = TestViewModel<MovieDetailsViewState, MovieDetailsEffect>()
        val module = module(testSuperheroService(listOf(hulkDto)), viewModel)

        with(module) {
            program(42, flowOf(Up))

            viewModel.effects.test {
                awaitItem() shouldBe NavigateUp
            }
        }
    }
})
