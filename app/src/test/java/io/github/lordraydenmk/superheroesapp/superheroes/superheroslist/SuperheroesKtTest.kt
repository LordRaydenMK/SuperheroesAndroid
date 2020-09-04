package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.Paginated
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.common.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.data.ResourceList
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import io.github.lordraydenmk.superheroesapp.superheroes.data.ThumbnailDto
import io.github.lordraydenmk.superheroesapp.superheroes.testSuperheroService
import io.github.lordraydenmk.superheroesapp.superheroes.testViewModel
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl.Companion.toHttpUrl

class SuperheroesKtTest : FunSpec({

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

    test("FirstLoad - service returns a single superhero - Loading then Content list with 1 item") {
        val superhero = superheroDto(42, "Ant Man", "https://antman", "jpg")
        val service = testSuperheroService(listOf(superhero))

        val viewModel = testViewModel<SuperheroesViewState, Long>()
        val module = object : SuperheroesDependencies, AppModule by AppModule.create(service),
            ViewModelAlgebra<SuperheroesViewState, Long> by viewModel {}

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
        val service = testSuperheroService(error)

        val viewModel = testViewModel<SuperheroesViewState, Long>()
        val module = object : SuperheroesDependencies, AppModule by AppModule.create(service),
            ViewModelAlgebra<SuperheroesViewState, Long> by viewModel {}

        val problem = Problem(R.string.error_unrecoverable, false)

        module.program(Observable.just(FirstLoad)).subscribe()

        viewModel.viewState.test()
            .awaitCount(2)
            .assertValues(Loading, problem)
            .assertNotComplete()
    }

    test("First load then refresh - service fails, then succeeds - Loading, Problem, Loading Content") {
        val error = Exception("Unauthorised")
        val superhero = superheroDto(42, "Ant Man", "https://antman", "jpg")
        val service = object : SuperheroesService {
            var i = 0
            override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> = when (i) {
                0 -> Single.error(error)
                1 -> Single.just(PaginatedEnvelope(200, "Marvel", Paginated(listOf(superhero))))
                else -> throw IllegalStateException("This should not happen")
            }.also { i++ }

            override fun getSuperheroDetails(characterId: Long): Single<PaginatedEnvelope<SuperheroDto>> =
                fail("This should not be called")
        }

        val viewModel = testViewModel<SuperheroesViewState, Long>()
        val module = object : SuperheroesDependencies, AppModule by AppModule.create(service),
            ViewModelAlgebra<SuperheroesViewState, Long> by viewModel {}

        val actions = PublishSubject.create<SuperheroesAction>()
        module.program(actions).subscribe()
        actions.onNext(FirstLoad)

        // Somehow this breaks Type Inference after adding rxjava2-extensions ¯\_(ツ)_/¯
        val test = viewModel.viewState.test()

        test.awaitCount(2)
            .assertValueAt(0, Loading)
            .assertValueAt(1) { it is Problem }

        actions.onNext(Refresh)

        test.awaitCount(4)
            .assertValueAt(2, Loading)
            .assertValueAt(3) { it is Content }
            .assertNotComplete()
    }

    test("ShowDetails - Details effect") {
        val viewModel = testViewModel<SuperheroesViewState, Long>()
        val module = object : SuperheroesDependencies,
            AppModule by AppModule.create(testSuperheroService(emptyList())),
            ViewModelAlgebra<SuperheroesViewState, Long> by viewModel {}

        with(module) {
            program(Observable.just(LoadDetails(42))).subscribe()
        }

        viewModel.effects.test()
            .awaitCount(1)
            .assertValue(42)
            .assertNotComplete()
    }
})
