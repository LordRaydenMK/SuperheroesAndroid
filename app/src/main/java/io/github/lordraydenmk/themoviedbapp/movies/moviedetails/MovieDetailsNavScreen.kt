package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.appModule
import io.github.lordraydenmk.themoviedbapp.common.observeIn
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.Screen
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun MovieDetailsNavScreen(
    movieId: MovieId,
    backStack: SnapshotStateList<Screen>,
    viewModel: MovieDetailsViewModel = viewModel()
) {
    val module = object : MovieDetailsModule,
        AppModule by LocalContext.current.appModule(),
        ViewModelAlgebra<MovieDetailsViewState, MovieDetailsEffect> by viewModel {}


    val actions = remember { Channel<MovieDetailsAction>(Channel.UNLIMITED) }
    with(module) {
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                program(movieId, actions.receiveAsFlow())
            }
        }
        LaunchedEffect(lifecycleOwner) {
            viewModel.effects.map { effect ->
                when (effect) {
                    is NavigateUp -> backStack.removeLastOrNull()
                }
            }.observeIn(lifecycleOwner)
        }
    }
    val state by viewModel.viewState.collectAsState(Loading)
    MovieDetailsScreen(state, movieId, actions)
}