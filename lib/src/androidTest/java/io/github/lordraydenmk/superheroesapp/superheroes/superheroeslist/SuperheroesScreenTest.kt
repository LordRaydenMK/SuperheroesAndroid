package io.github.lordraydenmk.superheroesapp.superheroes.superheroeslist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Content
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Loading
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Problem
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroViewEntity
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.EmptyCoroutineContext

@MediumTest
@RunWith(AndroidJUnit4::class)
class SuperheroesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val scope = CoroutineScope(EmptyCoroutineContext)

    @Test
    fun loadingState_progressBarDisplayed() {
        composeTestRule.setContent {
            SuperheroesScreen(emptyFlow(), Loading, actions = Channel())
        }

        superheroesScreen(composeTestRule) {
            assertLoadingDisplayed()
            assertContentHidden()
            assertErrorHidden()
        }
    }

    @Test
    fun recoverableProblemState_errorViewDisplayedWithRetryText() {
        composeTestRule.setContent {
            SuperheroesScreen(
                emptyFlow(),
                Problem(ErrorTextRes(R.string.error_recoverable_network)),
                actions = Channel()
            )
        }

        val errorText =
            "We could not connect to our server. Please check your internet connection \n\nTap to retry!"
        superheroesScreen(composeTestRule) {
            assertErrorDisplayed(errorText)
            assertContentHidden()
            assertLoadingHidden()
        }
    }

    @Test
    fun unrecoverableProblemState_errorViewDisplayed() {
        composeTestRule.setContent {
            SuperheroesScreen(
                emptyFlow(),
                Problem(ErrorTextRes(R.string.error_unrecoverable)),
                actions = Channel()
            )
        }

        val errorText = "Ooops… Something went wrong!"
        superheroesScreen(composeTestRule) {
            assertErrorDisplayed(errorText)
            assertContentHidden()
            assertLoadingHidden()
        }
    }

    @Test
    fun contentState_recyclerViewAndCopyrightViewDisplayed() {
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
        composeTestRule.setContent {
            SuperheroesScreen(emptyFlow(), viewState, actions = Channel())
        }

        superheroesScreen(composeTestRule) {
            assertContentDisplayed("Copyright Marvel")
            assertLoadingHidden()
            assertErrorHidden()
        }

    }
}