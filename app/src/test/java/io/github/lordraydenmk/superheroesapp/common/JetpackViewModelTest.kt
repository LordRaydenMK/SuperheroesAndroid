package io.github.lordraydenmk.superheroesapp.common

import app.cash.turbine.test
import io.github.lordraydenmk.superheroesapp.common.presentation.JetpackViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JetpackViewModelTest : FunSpec({

    test("setState - updates viewState") {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setState("Test")

        viewModel.viewState.test {
            awaitItem() shouldBe "Test"
        }
    }

    test("setState twice - keeps last state") {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setState("First")
        viewModel.setState("Second")

        viewModel.viewState.test {
            awaitItem() shouldBe "Second"
        }
    }

    test("isEmpty - new view model - true") {
        val viewModel = JetpackViewModel<String, Nothing>()

        viewModel.isEmpty() shouldBe true
    }

    test("isEmpty - view model with state - false") {
        val viewModel = JetpackViewModel<String, Nothing>()

        viewModel.setState("Hello world")

        viewModel.isEmpty() shouldBe false
    }

    test("runEffect - no subscribers - adds effect to queue") {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.runEffect("First")
        viewModel.runEffect("Second")


        viewModel.effects.test {
            awaitItem() shouldBe "First"
            awaitItem() shouldBe "Second"
        }
    }

    test("runEffect - subscriber - consumes effect") {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.effects.test {
            viewModel.runEffect("First")
            awaitItem() shouldBe "First"
        }

        viewModel.effects.test {
            expectNoEvents()
        }
    }
})
