package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

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

@ExperimentalCoroutinesApi
class MovieDetailsScreenTest {

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
            MovieDetailsScreen(
                stateFlow = emptyFlow(),
                initialState = Loading,
                movieId = 0,
                actions = Channel()
            )
        }
    }

    @Test
    fun errorStateWithRetry() {
        val viewState = Problem(ErrorTextRes(R.string.error_recoverable_network))

        paparazzi.snapshot {
            MovieDetailsScreen(emptyFlow(), viewState, movieId = 0, actions = Channel())
        }
    }

    @Test
    fun contentState() {
        val url = "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg".toHttpUrl()
        val viewState = Content(MovieDetailsViewEntity(
            "Hulk",
            "Movie overview",
            VoteAverage(0.745f, "7.5"),
            url
        ))

        paparazzi.snapshot {
            MovieDetailsScreen(emptyFlow(), viewState, movieId = 0, actions = Channel())
        }
    }
}