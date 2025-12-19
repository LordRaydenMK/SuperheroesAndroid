package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.lordraydenmk.themoviedbapp.AppModule
import io.github.lordraydenmk.themoviedbapp.BackStack
import io.github.lordraydenmk.themoviedbapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun MovieDetailsNavScreen(
    appModule: AppModule,
    movieId: MovieId,
    backStack: BackStack,
    viewModel: MovieDetailsViewModel = viewModel()
) {
    val module = remember {
        object : MovieDetailsModule,
            AppModule by appModule,
            ViewModelAlgebra<MovieDetailsViewState, MovieDetailsEffect> by viewModel {
            override val actions: Channel<MovieDetailsAction> = Channel(Channel.UNLIMITED)
        }
    }


    with(module) {
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                program(movieId, actions.receiveAsFlow())
            }
        }
        LaunchedEffect(lifecycleOwner) {
            viewModel.effects
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .map { effect ->
                    when (effect) {
                        is NavigateUp -> backStack.removeLastOrNull()
                    }
                }.collect()
        }
    }
    val state by viewModel.viewState.collectAsState(Loading)
    MovieDetailsScreen(state, movieId, module.actions)
}