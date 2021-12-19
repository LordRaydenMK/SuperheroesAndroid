package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.compose.LocalImageLoader
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.SuperheroDetailsFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class SuperheroesFragment : Fragment() {

    private val viewModel: SuperheroesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView

        val module: SuperheroesModule = object : SuperheroesModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}

        val actions = Channel<SuperheroesAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                CompositionLocalProvider(LocalImageLoader provides this) {
                    SuperheroesScreen(
                        stateFlow = viewState.onEach { afterBind(it) },
                        initialValue = Loading,
                        actions = actions
                    )
                }
            }

            lifecycleScope.launchWhenStarted {
                program(actions.receiveAsFlow()).collect()
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