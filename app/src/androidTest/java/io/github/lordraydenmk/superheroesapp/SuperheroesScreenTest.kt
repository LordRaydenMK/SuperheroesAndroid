package io.github.lordraydenmk.superheroesapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.lordraydenmk.superheroesapp.ScreenScenario.Companion.launchInContainer
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Content
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Loading
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.Problem
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroViewEntity
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroesScreen
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

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

        onView(withId(R.id.rvSuperheroes)).check(matches(isDisplayed()))
        onView(withId(R.id.copyrightLayout)).check(matches(withText("Copyright Marvel")))

        onView(withId(R.id.progressSuperheroes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvError)).check(matches(not(isDisplayed())))
    }
}