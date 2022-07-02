package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class SuperheroDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_progressBarDisplayed() {
        composeTestRule.setContent {
            SuperheroDetailsScreen(
                stateFlow = emptyFlow(),
                initialState = Loading,
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
            SuperheroDetailsScreen(emptyFlow(), viewState, superheroId = 0, actions = Channel())
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
        val viewState = Problem(IdTextRes(R.string.error_unrecoverable))

        composeTestRule.setContent {
            SuperheroDetailsScreen(emptyFlow(), viewState, superheroId = 0, actions = Channel())
        }

        superheroDetails(composeTestRule) {
            assertErrorDisplayed("Ooops… Something went wrong!")
            assertLoadingHidden()
            assertContentHidden()
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

        composeTestRule.setContent {
            SuperheroDetailsScreen(emptyFlow(), viewState, superheroId = 0, actions = Channel())
        }

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