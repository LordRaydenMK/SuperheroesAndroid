package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import androidx.compose.material3.ExperimentalMaterial3Api
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.MainDispatcherRule
import io.github.lordraydenmk.themoviedbapp.common.setupCoil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
class PopularMoviesScreenTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @get:Rule
    val rule = MainDispatcherRule()

    @Before
    fun setUp() {
        setupCoil(paparazzi)
    }

    @Test
    fun loadingState() {
        paparazzi.snapshot {
            PopularMoviesScreen(emptyFlow(), Loading, actions = Channel())
        }
    }

    @Test
    fun errorWithRetry() {
        paparazzi.snapshot {
            PopularMoviesScreen(
                emptyFlow(),
                Problem(ErrorTextRes(R.string.error_recoverable_network)),
                actions = Channel()
            )
        }
    }

    @Test
    fun content() {
        val url = "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg".toHttpUrl()
        val viewState = Content(
            listOf(
                MovieViewEntity(42, "Ant Man", url),
                MovieViewEntity(43, "Spider Man", url),
                MovieViewEntity(44, "Iron Man", url),
                MovieViewEntity(45, "Hulk", url)
            )
        )
        paparazzi.snapshot {
            PopularMoviesScreen(emptyFlow(), viewState, actions = Channel())
        }

    }
}