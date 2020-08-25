package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject

class SuperheroesViewModel : SuperheroesVM, ViewModel() {

    private val disposables = CompositeDisposable()

    private val _viewState = BehaviorSubject.create<SuperheroesViewState>()
        .toSerialized()

    override val viewState: Observable<SuperheroesViewState>
        get() = _viewState

    override fun setState(vs: SuperheroesViewState): Completable =
        Completable.fromCallable { _viewState.onNext(vs) }

    override fun addToDisposable(d: Disposable) {
        disposables += d
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}