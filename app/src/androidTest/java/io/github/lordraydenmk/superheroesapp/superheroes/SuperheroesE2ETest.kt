package io.github.lordraydenmk.superheroesapp.superheroes

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.github.lordraydenmk.superheroesapp.MainActivity
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.superheroDetails
import io.github.lordraydenmk.superheroesapp.superheroes.superheroeslist.superheroesScreen
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Content
import io.github.lordraydenmk.superheroesapp.utils.TestingModule
import io.github.lordraydenmk.superheroesapp.utils.awaitState
import io.github.lordraydenmk.superheroesapp.utils.enqueueJsonFromAssets
import io.github.lordraydenmk.superheroesapp.utils.replaceAppModule
import io.github.lordraydenmk.superheroesapp.utils.testModule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SuperheroesE2ETest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val server = MockWebServer()

    @Before
    fun setUp() {
        server.start()

        val url = server.url("/")
        replaceAppModule(testModule(url))

        server.enqueueJsonFromAssets("superheroes.json")
        server.enqueueJsonFromAssets("3dman.json")
    }

    @Test
    fun openList_openDetails_recreate_checkDetails_backToList() = runBlocking {
        launch(MainActivity::class.java).use { scenario ->

            val testModule: TestingModule = testModule()

            testModule.awaitState<Content>()

            superheroesScreen(composeTestRule) {
                assertContentDisplayed("Data provided by Marvel. © 2021 MARVEL")
                openSuperheroDetails("3-D Man")
            }
            superheroDetails(composeTestRule) {
                assertContentDisplayed(
                    comicsText = "Comics available: 12",
                    storiesText = "Stories available: 21",
                    eventsText = "Events available: 1",
                    seriesText = "Series available: 3",
                    copyrightText = "Data provided by Marvel. © 2021 MARVEL"
                )
            }
            scenario.recreate()
            superheroDetails(composeTestRule) {
                assertContentDisplayed(
                    comicsText = "Comics available: 12",
                    storiesText = "Stories available: 21",
                    eventsText = "Events available: 1",
                    seriesText = "Series available: 3",
                    copyrightText = "Data provided by Marvel. © 2021 MARVEL"
                )
            }
            Espresso.pressBack()
            superheroesScreen(composeTestRule) {
                assertContentDisplayed("Data provided by Marvel. © 2021 MARVEL")
            }
            Unit
        }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}