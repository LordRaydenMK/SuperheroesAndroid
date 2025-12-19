package io.github.lordraydenmk.themoviedbapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.lordraydenmk.themoviedbapp.movies.Screen
import io.github.lordraydenmk.themoviedbapp.movies.Screen.MovieDetails
import io.github.lordraydenmk.themoviedbapp.movies.Screen.PopularMovies
import io.github.lordraydenmk.themoviedbapp.movies.moviedetails.MovieDetailsNavScreen
import io.github.lordraydenmk.themoviedbapp.movies.popularmovies.MoviesNavScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
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
                            MoviesNavScreen(backStack)
                        }

                        is MovieDetails -> NavEntry(key) {
                            MovieDetailsNavScreen(key.movieId, backStack)
                        }
                    }
                }
            )
        }
    }
}