package io.github.lordraydenmk.superheroesapp.superheroes

import io.github.lordraydenmk.superheroesapp.common.Paginated
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroesVM
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroesViewState
import io.kotest.assertions.fail
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.ReplaySubject

fun testSuperheroService(superheroes: List<SuperheroDto>): SuperheroesService =
    object : SuperheroesService {

        override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> =
            Single.just(PaginatedEnvelope(200, "Marvel rocks!", Paginated(superheroes)))

        override fun getSuperheroDetails(characterId: Long): Single<PaginatedEnvelope<SuperheroDto>> =
            fail("This should not be called")
    }

fun testSuperheroService(t: Throwable): SuperheroesService = object : SuperheroesService {

    override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> = Single.error(t)

    override fun getSuperheroDetails(characterId: Long): Single<PaginatedEnvelope<SuperheroDto>> =
        fail("This should not be called")
}

fun testViewModel(): SuperheroesVM = object : SuperheroesVM {

    val cd = CompositeDisposable()

    private val _viewState = ReplaySubject.create<SuperheroesViewState>()
    override val viewState: Observable<SuperheroesViewState>
        get() = _viewState

    override fun setState(vs: SuperheroesViewState): Completable =
        Completable.fromCallable { _viewState.onNext(vs) }

    override fun addToDisposable(d: Disposable) {
        cd += d
    }
}