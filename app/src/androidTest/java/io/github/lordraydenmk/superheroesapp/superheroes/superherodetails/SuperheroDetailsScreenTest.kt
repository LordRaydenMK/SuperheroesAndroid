package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.ScreenScenario
import io.github.lordraydenmk.superheroesapp.ScreenScenario.Companion.launchInContainer
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import kotlinx.coroutines.channels.Channel
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class SuperheroDetailsScreenTest {

    // ScreenScenario assumes ConstraintLayout as root layout
    // SuperheroDetailsScreen need CoordinatorLayout
    // This is a workaround
    val launchInDecoratedContainer: () -> ScreenScenario<SuperheroDetailsScreen> = {
        launchInContainer { parent ->
            val coordinatorLayout = CoordinatorLayout(parent.context)
            parent.addView(coordinatorLayout)
            SuperheroDetailsScreen(coordinatorLayout, 42)
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun loadingState_progressBarDisplayed() {
        composeTestRule.setContent {
            SuperheroDetailsScreen(
                viewState = Loading,
                superheroId = 0,
                actions = Channel()
            )
        }

        superheroDetails(composeTestRule) {
            assertLoadingDisplayed()
            assertContentHidden()
            assertErrorHidden()
        }
    }

    @Test
    fun recoverableProblemState_errorViewDisplayedWithRetryText() {
        val viewState = Problem(ErrorTextRes(R.string.error_recoverable_network))

        composeTestRule.setContent {
            SuperheroDetailsScreen(viewState = viewState, superheroId = 0, actions = Channel())
        }

        val errorText =
            "We could not connect to our server. Please check your internet connection \n\nTap to retry!"
        superheroDetails(composeTestRule) {
            assertErrorDisplayed(errorText)
            assertLoadingHidden()
            assertContentHidden()
        }
    }

    @Test
    fun unrecoverableProblemState_errorViewDisplayed() {
        launchInDecoratedContainer().use { scenario ->
            scenario.onViewBlocking { view ->
                view.bind(Problem(IdTextRes(R.string.error_unrecoverable)))
            }

            superheroDetails(composeTestRule) {
                assertErrorDisplayed("Ooops… Something went wrong!")
                assertLoadingHidden()
                assertContentHidden()
            }
        }
    }

    @Test
    fun contentState_statsAndCopyrightViewDisplayed() {
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
        launchInDecoratedContainer().use { scenario ->
            scenario.onViewBlocking { view -> view.bind(viewState) }

            superheroDetails(composeTestRule) {
                assertContentDisplayed(
                    comicsText = "Comics available: 1",
                    storiesText = "Stories available: 2",
                    eventsText = "Events available: 3",
                    seriesText = "Series available: 4",
                    copyrightText = "Copyright Marvel"
                )
                assertLoadingHidden()
                assertErrorHidden()
            }
        }
    }
}