package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.superheroes.data.ResourceList
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.ThumbnailDto
import io.github.lordraydenmk.superheroesapp.superheroes.testSuperheroService
import io.github.lordraydenmk.superheroesapp.superheroes.testViewModel
import io.kotest.core.spec.style.FunSpec
import io.reactivex.Observable
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

class SuperheroDetailsKtTest : FunSpec({

    fun superheroDto(
        id: Long,
        name: String,
        thumbnailPath: String,
        thumbnailExt: String,
        comics: Int = 4,
        stories: Int = 6,
        events: Int = 8,
        series: Int = 2
    ): SuperheroDto =
        SuperheroDto(
            id,
            name,
            ThumbnailDto(thumbnailPath, thumbnailExt),
            ResourceList(comics),
            ResourceList(stories),
            ResourceList(events),
            ResourceList(series)
        )

    test("FirstLoad - service with success - Superhero") {
        val hulkDto = superheroDto(42, "Hulk", "https://hulk", "jpg", 1, 2, 3, 4)
        val service = testSuperheroService(listOf(hulkDto))

        val viewModel = testViewModel<SuperheroDetailsViewState, Unit>()
        val module = SuperheroDetailsDependencies.create(AppModule.create(service), viewModel)

        with(module) {
            program(Observable.just(FirstLoad(42))).subscribe()

            val hulk = SuperheroDetailsViewEntity(
                "Hulk",
                "https://hulk.jpg".toHttpUrl(),
                PlaceholderString(R.string.superhero_details_comics, 1),
                PlaceholderString(R.string.superhero_details_series, 4),
                PlaceholderString(R.string.superhero_details_events, 3),
                PlaceholderString(R.string.superhero_details_stories, 2)
            )
            viewModel.viewState
                .test()
                .awaitCount(2)
                .assertValueAt(0, Loading)
                .assertValueAt(1, Content(hulk, "Marvel rocks!"))
        }
    }

    test("FirstLoad - service with error - Problem") {
        val error = IOException("Bang")
        val service = testSuperheroService(error)

        val viewModel = testViewModel<SuperheroDetailsViewState, Unit>()
        val module = SuperheroDetailsDependencies.create(AppModule.create(service), viewModel)

        with(module) {
            program(Observable.just(FirstLoad(42))).subscribe()

            viewModel.viewState
                .test()
                .awaitCount(2)
                .assertValueAt(0, Loading)
                .assertValueAt(1, Problem(R.string.error_recoverable_network, Refresh(42)))
        }
    }
})
