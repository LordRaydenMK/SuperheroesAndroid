package io.github.lordraydenmk.superheroesapp.common

import app.cash.turbine.test
import io.github.lordraydenmk.superheroesapp.common.presentation.JetpackViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

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

    test("runInitialize - new view model - runs") {
        val viewModel = JetpackViewModel<String, Nothing>()

        var hasRun = false
        viewModel.runInitialize { hasRun = true }

        hasRun shouldBe true
    }

    test("runInitialize twice - executes lambda once") {
        val viewModel = JetpackViewModel<String, Nothing>()

        var count = 0
        viewModel.runInitialize { count++ }

        count shouldBe 1
    }

    test("runInitialize concurrently - executes lambda once") {
        val viewModel = JetpackViewModel<String, Nothing>()

        var count = 0
        coroutineScope {
            (0..100).map {
                async {
                    withContext(Dispatchers.Default) {
                        viewModel.runInitialize { count++ }
                    }
                }
            }.awaitAll()
        }

        count shouldBe 1
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
