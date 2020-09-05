package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.Event
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.autoDispose
import io.github.lordraydenmk.superheroesapp.common.evalOn
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.SuperheroDetailsFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SuperheroesFragment : Fragment(R.layout.superheroes_fragment) {

    private val viewModel by viewModels<SuperheroesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screen = SuperheroesScreen(view as ViewGroup)

        val module: SuperheroesModule = object : SuperheroesModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}

        with(module) {
            val renderObservable =
                viewState.switchMap {
                    screen.bind(it).toObservable<Unit>()
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

    override fun onStart() {
        super.onStart()

        viewModel.effects
            .ofType(NavigateToDetails::class.java)
            .flatMap {
                Observable.fromCallable {
                    findNavController().navigate(
                        R.id.action_details,
                        SuperheroDetailsFragment.newBundle(it.superheroId)
                    )
                }
            }
            .subscribe()
            .autoDispose(lifecycle, Event.ON_STOP)
    }
}