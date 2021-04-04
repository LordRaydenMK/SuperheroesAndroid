package io.github.lordraydenmk.superheroesapp.common

import app.cash.turbine.test
import io.github.lordraydenmk.superheroesapp.common.presentation.JetpackViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals

class JetpackViewModelTest : FunSpec({

    test("setState - updates viewState") {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setStateS("Test")

        viewModel.viewStateF.test {
            assertEquals("Test", expectItem())
        }
    }

    test("setState twice - keeps last state") {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setStateS("First")
        viewModel.setStateS("Second")

        viewModel.viewStateF.test {
            assertEquals("Second", expectItem())
        }
    }

    test("isEmpty - new view model - true") {
        val viewModel = JetpackViewModel<String, Nothing>()

        viewModel.isEmpty() shouldBe true
    }

    test("isEmpty - view model with state - false") {
        val viewModel = JetpackViewModel<String, Nothing>()

        viewModel.setStateS("Hello world")

        viewModel.isEmpty() shouldBe false
    }

    test("runEffect - no subscribers - adds effect to queue") {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.runEffectS("First")
        viewModel.runEffectS("Second")


        viewModel.effectsF.test {
            assertEquals("First", expectItem())
            assertEquals("Second", expectItem())
        }
    }

    test("runEffect - subscriber - consumes effect") {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.effectsF.test {
            viewModel.runEffectS("First")
            assertEquals("First", expectItem())
        }

        viewModel.effectsF.test {
            expectNoEvents()
        }
    }
})
