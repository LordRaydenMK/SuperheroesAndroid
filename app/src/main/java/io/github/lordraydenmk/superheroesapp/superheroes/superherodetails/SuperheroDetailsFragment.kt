package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.observeIn
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SuperheroDetailsFragment : Fragment(R.layout.fragment_compose) {

    private val superheroId: Long by lazy(LazyThreadSafetyMode.NONE) {
        val id = requireArguments().getLong(EXTRA_SUPERHERO_ID, -1)
        check(id != -1L) { "Please use newBundle() for creating the arguments" }
        id
    }

    private val viewModel: SuperheroDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView
        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)

        val module = object : SuperheroDetailsModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect> by viewModel {}

        val actions = Channel<SuperheroDetailsAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                SuperheroDetailsScreen(
                    stateFlow = viewState,
                    initialState = Loading,
                    superheroId = superheroId,
                    actions = actions
                )
            }

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(State.STARTED) {
                    program(superheroId, actions.receiveAsFlow())
                }
            }
        }

        handleEffects()
    }

    private fun handleEffects() {
        viewModel.effects.map { effect ->
            when (effect) {
                NavigateUp -> findNavController().navigateUp()
            }
        }.observeIn(this)
    }

    companion object {

        private const val EXTRA_SUPERHERO_ID = "EXTRA_SUPERHERO_ID"

        fun newBundle(superheroId: SuperheroId): Bundle =
            Bundle().apply {
                putLong(EXTRA_SUPERHERO_ID, superheroId)
            }
    }
}