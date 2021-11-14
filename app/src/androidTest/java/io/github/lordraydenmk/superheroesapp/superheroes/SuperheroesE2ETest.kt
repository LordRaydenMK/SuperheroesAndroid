package io.github.lordraydenmk.superheroesapp.superheroes

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.github.lordraydenmk.superheroesapp.MainActivity
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.superheroeslist.superheroesScreen
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Content
import io.github.lordraydenmk.superheroesapp.utils.TestingModule
import io.github.lordraydenmk.superheroesapp.utils.enqueueJsonFromAssets
import io.github.lordraydenmk.superheroesapp.utils.replaceAppModule
import io.github.lordraydenmk.superheroesapp.utils.testModule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@LargeTest
@RunWith(AndroidJUnit4::class)
class SuperheroesE2ETest {

    private val server = MockWebServer()

    @Before
    fun setUp() {
        server.start()

        val url = server.url("/")
        replaceAppModule(testModule(url))

        server.enqueueJsonFromAssets("superheroes.json")
        server.enqueueJsonFromAssets("3dman.json")
    }

    suspend fun <T> TestingModule.awaitState(clazz: Class<T>): Unit = suspendCoroutine { cont ->
        Timber.d("awaitClose clazz = $clazz")
        if (state.javaClass == clazz)
            cont.resume(Unit).also { Timber.d("resume Unit") }
        else {
            afterBind = { state: Any ->
                if (state.javaClass == clazz) cont.resume(Unit).also { Timber.d("resume Unit after") }
                else cont.resumeWithException(RuntimeException("Wrong state")).also { Timber.d("resume RuntimeEx") }
            }
        }
    }

    @Test
    fun openList_openDetails_recreate_checkDetails_backToList() =
        launch(MainActivity::class.java).use { scenario ->

            var testModule: TestingModule? = null
            scenario.onActivity {
                testModule = it.appModule() as TestingModule
            }

            runBlocking { testModule!!.awaitState(Content::class.java) }

            superheroesScreen {
                assertContentDisplayed("Data provided by Marvel. © 2021 MARVEL")
                openSuperheroDetails()
            }
            superheroDetails {
                assertContentDisplayed(
                    comicsText = "Comics available: 12",
                    storiesText = "Stories available: 21",
                    eventsText = "Events available: 1",
                    seriesText = "Series available: 3",
                    copyrightText = "Data provided by Marvel. © 2021 MARVEL"
                )
            }
            scenario.recreate()
            superheroDetails {
                assertContentDisplayed(
                    comicsText = "Comics available: 12",
                    storiesText = "Stories available: 21",
                    eventsText = "Events available: 1",
                    seriesText = "Series available: 3",
                    copyrightText = "Data provided by Marvel. © 2021 MARVEL"
                )
            }
            Espresso.pressBack()
            superheroesScreen {
                assertContentDisplayed("Data provided by Marvel. © 2021 MARVEL")
            }
            Unit
        }

    @After
    fun tearDown() {
        server.shutdown()
    }
}