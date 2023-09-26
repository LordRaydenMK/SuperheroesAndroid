package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import coil.annotation.ExperimentalCoilApi
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.MainDispatcherRule
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
class SuperheroesScreenTest {

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
            SuperheroesScreen(emptyFlow(), Loading, actions = Channel())
        }
    }

    @Test
    fun errorWithRetry() {
        paparazzi.snapshot {
            SuperheroesScreen(
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
                SuperheroViewEntity(42, "Ant Man", url),
                SuperheroViewEntity(43, "Spider Man", url),
                SuperheroViewEntity(44, "Iron Man", url),
                SuperheroViewEntity(45, "Hulk", url)
            ),
            "Copyright Marvel"
        )
        paparazzi.snapshot {
            SuperheroesScreen(emptyFlow(), viewState, actions = Channel())
        }

    }
}