package io.github.lordraydenmk.themoviedbapp.common

import app.cash.turbine.test
import io.github.lordraydenmk.themoviedbapp.common.presentation.JetpackViewModel
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test

class JetpackViewModelTest {

    @Test
    fun `setState - updates viewState`() = runTest {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setState("Test")

        viewModel.viewState.test {
            awaitItem() shouldBe "Test"
        }
    }

    @Test
    fun `setState twice - keeps last state`() = runTest {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setState("First")
        viewModel.setState("Second")

        viewModel.viewState.test {
            awaitItem() shouldBe "Second"
        }
    }

    @Test
    fun `runInitialize - new view model - runs`() = runTest {
        val viewModel = JetpackViewModel<String, Nothing>()

        var hasRun = false
        viewModel.runInitialize { hasRun = true }

        hasRun shouldBe true
    }

    @Test
    fun `runInitialize twice - executes lambda once`() = runTest {
        val viewModel = JetpackViewModel<String, Nothing>()

        var count = 0
        viewModel.runInitialize { count++ }
        viewModel.runInitialize { count++ } // Second call to ensure it only runs once

        count shouldBe 1
    }

    @Test
    fun `runInitialize concurrently - executes lambda once`() = runTest {
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

    @Test
    fun `runEffect - no subscribers - adds effect to queue`() = runTest {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.runEffect("First")
        viewModel.runEffect("Second")

        viewModel.effects.test {
            awaitItem() shouldBe "First"
            awaitItem() shouldBe "Second"
        }
    }

    @Test
    fun `runEffect - subscriber - consumes effect`() = runTest {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.effects.test {
            viewModel.runEffect("First")
            awaitItem() shouldBe "First"
        }

        viewModel.effects.test {
            expectNoEvents()
        }
    }
}
