package io.github.lordraydenmk.superheroesapp.superheroes.superheroeslist

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.ScreenScenario.Companion.launchInContainer
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Content
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Loading
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Problem
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroViewEntity
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroesScreen
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class SuperheroesScreenTest {

    @Test
    fun loadingState_progressBarDisplayed() {
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }
        scenario.onView { view -> view.bind(Loading).subscribe() }

        superheroesScreen {
            assertLoadingDisplayed()
            assertContentHidden()
            assertErrorHidden()
        }
    }

    @Test
    fun recoverableProblemState_errorViewDisplayedWithRetryText() {
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }
        scenario.onView { view ->
            view.bind(Problem(ErrorTextRes(R.string.error_recoverable_network))).subscribe()
        }

        val errorText =
            "We could not connect to our server. Please check your internet connection \n\nTap to retry!"
        superheroesScreen {
            assertErrorDisplayed(errorText)
            assertContentHidden()
            assertLoadingHidden()
        }
    }

    @Test
    fun unrecoverableProblemState_errorViewDisplayed() {
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }
        scenario.onView { view ->
            view.bind(Problem(IdTextRes(R.string.error_unrecoverable))).subscribe()
        }

        val errorText = "Ooops… Something went wrong!"
        superheroesScreen {
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
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }

        scenario.onView { view -> view.bind(viewState).subscribe() }

        superheroesScreen {
            assertContentDisplayed("Copyright Marvel")
            assertLoadingHidden()
            assertErrorHidden()
        }
    }
}