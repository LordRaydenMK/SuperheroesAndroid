package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

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
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.appModule
import io.github.lordraydenmk.themoviedbapp.common.observeIn
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MovieDetailsFragment : Fragment(R.layout.fragment_compose) {

    private val movieId: Long by lazy(LazyThreadSafetyMode.NONE) {
        val id = requireArguments().getLong(EXTRA_MOVIE_ID, -1)
        check(id != -1L) { "Please use newBundle() for creating the arguments" }
        id
    }

    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView
        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)

        val module = object : MovieDetailsModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<MovieDetailsViewState, MovieDetailsEffect> by viewModel {}

        val actions = Channel<MovieDetailsAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                MovieDetailsScreen(
                    stateFlow = viewState,
                    initialState = Loading,
                    movieId = movieId,
                    actions = actions
                )
            }

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(State.STARTED) {
                    program(movieId, actions.receiveAsFlow())
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

        private const val EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID"

        fun newBundle(movieId: MovieId): Bundle =
            Bundle().apply {
                putLong(EXTRA_MOVIE_ID, movieId)
            }
    }
}