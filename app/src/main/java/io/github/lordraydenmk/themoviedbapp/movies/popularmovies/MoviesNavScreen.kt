package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.appModule
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesNavScreen(
    backStack: SnapshotStateList<Screen>,
    viewModel: MoviesViewModel = viewModel()
) {
    val module = object : TheMovieDbModule,
        AppModule by LocalContext.current.appModule(),
        ViewModelAlgebra<PopularMoviesViewState, MoviesEffect> by viewModel {}

    val actions = remember { Channel<MoviesAction>(Channel.UNLIMITED) }

    with(module) {
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                program(actions.receiveAsFlow())
            }
        }
        LaunchedEffect(lifecycleOwner) {
            viewModel.effects
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .map { effect ->
                    when (effect) {
                        is NavigateToDetails -> backStack.add(Screen.MovieDetails(effect.movieId))
                    }
                }.collect()
        }
    }

    val state by viewModel.viewState.collectAsState(Loading)
    PopularMoviesScreen(state = state, actions)
}