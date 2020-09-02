package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.Event
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.autoDispose
import io.github.lordraydenmk.superheroesapp.common.evalOn
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SuperheroDetailsFragment : Fragment(R.layout.superhero_details_fragment) {

    private val superheroId: Long by lazy(LazyThreadSafetyMode.NONE) {
        val id = requireArguments().getLong(EXTRA_SUPERHERO_ID, -1)
        check(id != -1L) { "Please use newBundle() for creating the arguments" }
        id
    }

    private val viewModel by viewModels<SuperheroDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screen = SuperheroDetailsScreen(view as ViewGroup, superheroId)

        val module = SuperheroDetailsDependencies.create(requireActivity().appModule(), viewModel)

        with(module) {
            val firstLoad = Observable.just(savedInstanceState == null)
                .filter { it }
                .map<SuperheroDetailsAction> { FirstLoad(superheroId) }

            val render = viewState.switchMap {
                screen.bind(it)
                    .toObservable<Unit>()
                    .evalOn(AndroidSchedulers.mainThread())
            }

            program(firstLoad.mergeWith(screen.actions))
                .mergeWith(render)
                .subscribe()
                .autoDispose(viewLifecycleOwner.lifecycle)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.effects
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { Observable.fromCallable { findNavController().navigateUp() } }
            .subscribe()
            .autoDispose(lifecycle, Event.ON_STOP)
    }

    companion object {

        private const val EXTRA_SUPERHERO_ID = "EXTRA_SUPERHERO_ID"

        fun newBundle(superheroId: SuperheroId): Bundle =
            Bundle().apply {
                putLong(EXTRA_SUPERHERO_ID, superheroId)
            }
    }
}