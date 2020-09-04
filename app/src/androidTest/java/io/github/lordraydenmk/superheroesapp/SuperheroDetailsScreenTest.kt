package io.github.lordraydenmk.superheroesapp

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.lordraydenmk.superheroesapp.ScreenScenario.Companion.launchInContainer
import io.github.lordraydenmk.superheroesapp.common.PlaceholderString
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

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

    @Test
    fun loadingState_progressBarDisplayed() {
        val scenario = launchInDecoratedContainer()
        scenario.onView { view -> view.bind(Loading).subscribe() }

        onView(withId(R.id.progress)).check(matches(isDisplayed()))

        onView(withId(R.id.tvComicsCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvSeriesCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvStoriesCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvEventsCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.copyrightLayout)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tvError)).check(matches(not(isDisplayed())))
    }

    @Test
    fun recoverableProblemState_errorViewDisplayedWithRetryText() {
        val scenario = launchInDecoratedContainer()
        scenario.onView { view ->
            view.bind(
                Problem(
                    R.string.error_recoverable_network,
                    Refresh(42)
                )
            ).subscribe()
        }

        onView(withId(R.id.tvError)).check(matches(isDisplayed()))
        onView(withId(R.id.tvError)).check(matches(withText("We could not connect to our server. Please check your internet connection \n\nTap to retry!")))

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tvComicsCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvSeriesCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvStoriesCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvEventsCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.copyrightLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun unrecoverableProblemState_errorViewDisplayed() {
        val scenario = launchInDecoratedContainer()
        scenario.onView { view ->
            view.bind(Problem(R.string.error_unrecoverable, null)).subscribe()
        }

        onView(withId(R.id.tvError)).check(matches(isDisplayed()))
        onView(withId(R.id.tvError)).check(matches(withText("Ooops… Something went wrong!")))

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tvComicsCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvSeriesCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvStoriesCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvEventsCount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.copyrightLayout)).check(matches(not(isDisplayed())))
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
        val scenario = launchInDecoratedContainer()

        scenario.onView { view -> view.bind(viewState).subscribe() }

        onView(withId(R.id.tvComicsCount)).check(matches(withText("Comics available: 1")))
        onView(withId(R.id.tvStoriesCount)).check(matches(withText("Stories available: 2")))
        onView(withId(R.id.tvEventsCount)).check(matches(withText("Events available: 3")))
        onView(withId(R.id.tvSeriesCount)).check(matches(withText("Series available: 4")))
        onView(withId(R.id.copyrightLayout)).check(matches(withText("Copyright Marvel")))

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvError)).check(matches(not(isDisplayed())))
    }
}