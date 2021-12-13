package io.github.lordraydenmk.superheroesapp.superheroes.superheroeslist

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

fun superheroesScreen(rule: ComposeTestRule, f: SuperheroesScreenRobot.() -> Unit) =
    SuperheroesScreenRobot(rule).also(f)

class SuperheroesScreenRobot(private val rule: ComposeTestRule) {

    fun assertLoadingDisplayed() {
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    fun assertErrorDisplayed(errorText: String) {
        rule.onNodeWithText(errorText).assertIsDisplayed()
    }

    fun assertContentDisplayed(copyrightText: String) {
        rule.onNodeWithTag("SuperheroesContent").assertIsDisplayed()
        rule.onNodeWithText(copyrightText).assertIsDisplayed()
    }

    fun assertLoadingHidden() {
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertDoesNotExist()
    }

    fun assertContentHidden() {
        rule.onNodeWithTag("SuperheroesContent").assertDoesNotExist()
    }

    fun assertErrorHidden() {
        rule.onNodeWithTag("SuperheroProblem").assertDoesNotExist()
    }

    fun openSuperheroDetails(superheroName: String) {
        rule.onNodeWithText(superheroName).performClick()
    }
}