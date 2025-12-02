package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import android.os.Bundle
import android.view.View
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.appModule
import io.github.lordraydenmk.themoviedbapp.common.observeIn
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.moviedetails.MovieDetailsFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class MoviesFragment : Fragment(R.layout.fragment_compose) {

    private val viewModel: MoviesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView
        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)

        val module: TheMovieDbModule = object : TheMovieDbModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<PopularMoviesViewState, MoviesEffect> by viewModel {}

        val actions = Channel<MoviesAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                PopularMoviesScreen(
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
                    MovieDetailsFragment.newBundle(effect.movieId)
                )
            }
        }.observeIn(this)
    }
}