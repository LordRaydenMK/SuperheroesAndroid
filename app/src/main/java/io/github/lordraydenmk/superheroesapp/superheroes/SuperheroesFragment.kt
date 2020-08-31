package io.github.lordraydenmk.superheroesapp.superheroes

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.autoDispose
import io.github.lordraydenmk.superheroesapp.common.evalOn
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SuperheroesFragment : Fragment(R.layout.superheroes_fragment) {

    private val viewModel by viewModels<SuperheroesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screen = SuperheroesScreen(view as ViewGroup)

        val module: SuperheroesDependencies = object : SuperheroesDependencies,
            AppModule by requireActivity().appModule(),
            SuperheroesVM by viewModel {}

        with(module) {
            val renderObservable =
                viewState.switchMap {
                    Observable.fromCallable { screen.bind(it) }
                        .evalOn(AndroidSchedulers.mainThread())
                }

            val firstLoadAction =
                Observable.just(savedInstanceState == null)
                    .filter { it }
                    .map { FirstLoad }

            program(Observable.merge(screen.actions, firstLoadAction))
                .mergeWith(renderObservable)
                .subscribe()
                .autoDispose(viewLifecycleOwner.lifecycle)
        }
    }
}