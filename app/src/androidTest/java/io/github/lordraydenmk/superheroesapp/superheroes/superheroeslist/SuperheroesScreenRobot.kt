package io.github.lordraydenmk.superheroesapp.superheroes.superheroeslist

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.github.lordraydenmk.superheroesapp.R
import org.hamcrest.CoreMatchers.not

fun superheroesScreen(f: SuperheroesScreenRobot.() -> Unit) =
    SuperheroesScreenRobot().also(f)

class SuperheroesScreenRobot {

    private val loadingViewId = R.id.progressSuperheroes
    private val recyclerViewId = R.id.rvSuperheroes
    private val copyrightViewId = R.id.copyrightLayout
    private val errorViewId = R.id.tvError

    fun assertLoadingDisplayed() {
        onView(withId(loadingViewId))
            .check(matches(isDisplayed()))
    }

    fun assertErrorDisplayed(errorText: String) {
        onView(withId(errorViewId)).check(matches(isDisplayed()))
        onView(withId(errorViewId)).check(matches(withText(errorText)))
    }

    fun assertContentDisplayed(copyrightText: String) {
        onView(withId(R.id.rvSuperheroes)).check(matches(isDisplayed()))
        onView(withId(R.id.copyrightLayout)).check(matches(withText(copyrightText)))
    }

    fun assertLoadingHidden() {
        onView(withId(loadingViewId)).check(matches(not(isDisplayed())))
    }

    fun assertContentHidden() {
        onView(withId(recyclerViewId)).check(matches(not(isDisplayed())))
        onView(withId(copyrightViewId)).check(matches(not(isDisplayed())))
    }

    fun assertErrorHidden() {
        onView(withId(errorViewId)).check(matches(not(isDisplayed())))
    }
}