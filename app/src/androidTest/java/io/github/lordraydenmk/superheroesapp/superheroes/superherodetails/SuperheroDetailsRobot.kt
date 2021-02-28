package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.github.lordraydenmk.superheroesapp.R
import org.hamcrest.CoreMatchers.not

fun superheroDetails(f: SuperheroDetailsRobot.() -> Unit) =
    SuperheroDetailsRobot().also(f)

class SuperheroDetailsRobot {

    private val progressViewId = R.id.progress

    private val comicsViewId = R.id.tvComicsCount
    private val seriesViewId = R.id.tvSeriesCount
    private val storiesViewId = R.id.tvStoriesCount
    private val eventsViewId = R.id.tvEventsCount
    private val copyrightViewId = R.id.copyrightLayout

    private val errorViewId = R.id.tvError

    fun assertLoadingDisplayed() {
        onView(withId(progressViewId)).check(matches(isDisplayed()))
    }

    fun assertErrorDisplayed(errorText: String) {
        onView(withId(errorViewId)).check(matches(isDisplayed()))
        onView(withId(errorViewId)).check(matches(withText(errorText)))
    }

    fun assertContentDisplayed(
        comicsText: String,
        storiesText: String,
        eventsText: String,
        seriesText: String,
        copyrightText: String
    ) {
        onView(withId(comicsViewId)).check(matches(withText(comicsText)))
        onView(withId(storiesViewId)).check(matches(withText(storiesText)))
        onView(withId(eventsViewId)).check(matches(withText(eventsText)))
        onView(withId(seriesViewId)).check(matches(withText(seriesText)))
        onView(withId(copyrightViewId)).check(matches(withText(copyrightText)))
    }

    fun assertLoadingHidden() {
        onView(withId(progressViewId)).check(matches(not(isDisplayed())))
    }

    fun assertContentHidden() {
        onView(withId(comicsViewId)).check(matches(not(isDisplayed())))
        onView(withId(seriesViewId)).check(matches(not(isDisplayed())))
        onView(withId(storiesViewId)).check(matches(not(isDisplayed())))
        onView(withId(eventsViewId)).check(matches(not(isDisplayed())))
        onView(withId(copyrightViewId)).check(matches(not(isDisplayed())))
    }

    fun assertErrorHidden() {
        onView(withId(errorViewId)).check(matches(not(isDisplayed())))
    }
}