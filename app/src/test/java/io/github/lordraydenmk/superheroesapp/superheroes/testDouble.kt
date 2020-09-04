package io.github.lordraydenmk.superheroesapp.superheroes

import io.github.lordraydenmk.superheroesapp.common.Paginated
import io.github.lordraydenmk.superheroesapp.common.PaginatedEnvelope
import io.github.lordraydenmk.superheroesapp.common.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroDto
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
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
            Single.just(PaginatedEnvelope(200, "Marvel rocks!", Paginated(superheroes)))
    }

fun testSuperheroService(t: Throwable): SuperheroesService = object : SuperheroesService {

    override fun getSuperheroes(): Single<PaginatedEnvelope<SuperheroDto>> = Single.error(t)

    override fun getSuperheroDetails(characterId: Long): Single<PaginatedEnvelope<SuperheroDto>> =
        Single.error(t)
}

fun <VS, E> testViewModel(): ViewModelAlgebra<VS, E> = object : ViewModelAlgebra<VS, E> {

    val cd = CompositeDisposable()

    private val _viewState = ReplaySubject.create<VS>()
    override val viewState: Observable<VS>
        get() = _viewState

    override fun setState(vs: VS): Completable =
        Completable.fromCallable { _viewState.onNext(vs) }

    override fun addToDisposable(d: Disposable) {
        cd += d
    }

    private val _effects = ReplaySubject.create<E>()
    override val effects: Observable<E>
        get() = _effects

    override fun runEffect(effect: E): Completable =
        Completable.fromCallable { _effects.onNext(effect) }
}