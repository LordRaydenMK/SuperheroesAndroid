package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
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

        module.program(Observable.empty()).subscribe()

        viewModel.viewState.test()
            .awaitCount(2)
            .assertValues(Loading, content)
            .assertNotComplete()
    }

    test("FirstLoad - service fails with exception - Loading then Problem") {
        val error = Exception("Unauthorised")
        val service = testSuperheroService(error)

        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(service, viewModel)

        val problem = Problem(IdTextRes(R.string.error_unrecoverable))

        module.program(Observable.empty()).subscribe()

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

        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(service, viewModel)

        val actions = PublishSubject.create<SuperheroesAction>()
        module.program(actions).subscribe()

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

    test("ShowDetailsAction - NavigateToDetails effect") {
        val viewModel = TestViewModel<SuperheroesViewState, SuperheroesEffect>()
        val module = testModule(testSuperheroService(emptyList()), viewModel)

        with(module) {
            program(Observable.just(LoadDetails(42))).subscribe()
        }

        viewModel.effects.test()
            .awaitCount(1)
            .assertValue(NavigateToDetails(42))
            .assertNotComplete()
    }
})
