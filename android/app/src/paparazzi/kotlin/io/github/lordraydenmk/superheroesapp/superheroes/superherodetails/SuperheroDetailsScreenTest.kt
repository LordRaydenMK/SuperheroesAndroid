package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import coil.annotation.ExperimentalCoilApi
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.MainDispatcherRule
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.common.setupCoil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
class SuperheroDetailsScreenTest {

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
            SuperheroDetailsScreen(
                stateFlow = emptyFlow(),
                initialState = Loading,
                superheroId = 0,
                actions = Channel()
            )
        }
    }

    @Test
    fun errorStateWithRetry() {
        val viewState = Problem(ErrorTextRes(R.string.error_recoverable_network))

        paparazzi.snapshot {
            SuperheroDetailsScreen(emptyFlow(), viewState, superheroId = 0, actions = Channel())
        }
    }

    @Test
    fun contentState() {
        val url = "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg".toHttpUrl()
        val viewState = Content(
            SuperheroDetailsViewEntity(
                "Hulk",
                url,
                PlaceholderString(R.string.superhero_details_comics, 1),
                PlaceholderString(R.string.superhero_details_stories, 2),
                PlaceholderString(R.string.superhero_details_events, 3),
                PlaceholderString(R.string.superhero_details_series, 4)
            ),
            "Copyright Marvel"
        )

        paparazzi.snapshot {
            SuperheroDetailsScreen(emptyFlow(), viewState, superheroId = 0, actions = Channel())
        }
    }
}