package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText

fun superheroDetails(rule: ComposeTestRule, f: SuperheroDetailsRobot.() -> Unit) =
    SuperheroDetailsRobot(rule).also(f)

class SuperheroDetailsRobot(private val rule: ComposeTestRule) {

    fun assertLoadingDisplayed() {
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    fun assertErrorDisplayed(errorText: String) {
        rule.onNodeWithText(errorText).assertIsDisplayed()
    }

    fun assertContentDisplayed(
        comicsText: String,
        storiesText: String,
        eventsText: String,
        seriesText: String,
        copyrightText: String
    ) {
        rule.onNodeWithText(comicsText).assertIsDisplayed()
        rule.onNodeWithText(storiesText).assertIsDisplayed()
        rule.onNodeWithText(eventsText).assertIsDisplayed()
        rule.onNodeWithText(seriesText).assertIsDisplayed()
        rule.onNodeWithText(copyrightText).assertIsDisplayed()
    }

    fun assertLoadingHidden() {
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertDoesNotExist()
    }

    fun assertContentHidden() {
        rule.onNodeWithTag("SuperheroDetailsContent").assertDoesNotExist()
    }

    fun assertErrorHidden() {
        rule.onNodeWithTag("SuperheroProblem").assertDoesNotExist()
    }
}