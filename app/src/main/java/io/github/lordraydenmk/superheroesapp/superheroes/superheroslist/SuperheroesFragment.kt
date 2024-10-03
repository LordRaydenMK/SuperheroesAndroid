package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.observeIn
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.SuperheroDetailsFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SuperheroesFragment : Fragment(R.layout.fragment_compose) {

    private val viewModel: SuperheroesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView
        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)

        val module: SuperheroesModule = object : SuperheroesModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}

        val actions = Channel<SuperheroesAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                SuperheroesScreen(
                    stateFlow = viewState,
                    initialValue = Loading,
                    actions = actions
                )
            }

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    program(actions.receiveAsFlow())
                }
            }
        }

        handleEffects()
    }

    private fun handleEffects() {
        viewModel.effects.map { effect ->
            when (effect) {
                is NavigateToDetails -> findNavController().navigate(
                    R.id.action_details,
                    SuperheroDetailsFragment.newBundle(effect.superheroId)
                )
            }
        }.observeIn(this)
    }
}