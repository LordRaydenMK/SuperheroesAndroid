package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.SuperheroDetailsFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge

class SuperheroesFragment : Fragment(R.layout.superheroes_fragment) {

    private val viewModel by viewModels<SuperheroesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screen = SuperheroesScreen(view as ViewGroup, lifecycleScope)

        val module: SuperheroesModule = object : SuperheroesModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}

        with(module) {
            val renderFlow = viewState
                .mapLatest { state -> screen.bind(state).also { afterBind(state) } }

            lifecycleScope.launchWhenStarted {
                merge(program(screen.actions), renderFlow)
                    .collect()
            }
        }

        handleEffects()
    }

    private fun handleEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effects.map { effect ->
                when (effect) {
                    is NavigateToDetails -> findNavController().navigate(
                        R.id.action_details,
                        SuperheroDetailsFragment.newBundle(effect.superheroId)
                    )
                }
            }.collect()
        }
    }
}