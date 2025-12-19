package io.github.lordraydenmk.themoviedbapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.navigation3.ui.NavDisplay
import io.github.lordraydenmk.themoviedbapp.movies.Screen
import io.github.lordraydenmk.themoviedbapp.movies.Screen.MovieDetails
import io.github.lordraydenmk.themoviedbapp.movies.Screen.PopularMovies
import io.github.lordraydenmk.themoviedbapp.movies.moviedetails.MovieDetailsNavScreen
import io.github.lordraydenmk.themoviedbapp.movies.popularmovies.MoviesNavScreen

typealias BackStack = NavBackStack<Screen>

@Composable
fun <T : NavKey> rememberNavBackStack(vararg elements: NavKey): NavBackStack<T> {
    return rememberSerializable(
        serializer = NavBackStackSerializer(elementSerializer = NavKeySerializer())
    ) {
        @Suppress("UNCHECKED_CAST")
        NavBackStack(*elements) as NavBackStack<T>
    }
}

@Composable
fun MoviesApplication(appModule: AppModule) {
    val backStack = rememberNavBackStack<Screen>(PopularMovies)
    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
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