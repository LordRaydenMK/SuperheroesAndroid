package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

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
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge

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

        val module = object : SuperheroDetailsModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect> by viewModel {}

        with(module) {
            val render = viewState
                .mapLatest { screen.bind(it) }

            lifecycleScope.launchWhenStarted {
                merge(program(superheroId, screen.actions), render)
                    .collect()
            }
        }

        handleEffects()
    }

    private fun handleEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effects.map { effect ->
                when (effect) {
                    NavigateUp -> findNavController().navigateUp()
                }
            }
        }
    }

    companion object {

        private const val EXTRA_SUPERHERO_ID = "EXTRA_SUPERHERO_ID"

        fun newBundle(superheroId: SuperheroId): Bundle =
            Bundle().apply {
                putLong(EXTRA_SUPERHERO_ID, superheroId)
            }
    }
}