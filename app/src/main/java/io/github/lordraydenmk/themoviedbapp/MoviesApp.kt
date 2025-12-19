package io.github.lordraydenmk.themoviedbapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.lordraydenmk.themoviedbapp.movies.Screen
import io.github.lordraydenmk.themoviedbapp.movies.Screen.MovieDetails
import io.github.lordraydenmk.themoviedbapp.movies.Screen.PopularMovies
import io.github.lordraydenmk.themoviedbapp.movies.moviedetails.MovieDetailsNavScreen
import io.github.lordraydenmk.themoviedbapp.movies.popularmovies.MoviesNavScreen

typealias BackStack = SnapshotStateList<Screen>

@Composable
fun MoviesApplication(appModule: AppModule) {
    val backStack = remember { mutableStateListOf<Screen>(PopularMovies) }

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key: Screen ->
            when (key) {
                PopularMovies -> NavEntry(key) {
                    MoviesNavScreen(appModule, backStack)
                }

                is MovieDetails -> NavEntry(key) {
                    MovieDetailsNavScreen(appModule, key.movieId, backStack)
                }
            }
        }
    )
}