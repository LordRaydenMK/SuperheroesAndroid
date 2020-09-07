package io.github.lordraydenmk.superheroesapp.common

import io.kotest.core.spec.style.FunSpec

class JetpackViewModelTest : FunSpec({

    test("setState - updates viewState") {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setState("Test")
            .subscribe()

        viewModel.viewState.test()
            .assertValue("Test")
            .assertNotComplete()
    }

    test("setState twice - keeps last state") {
        val viewModel = JetpackViewModel<String, Nothing>()
        viewModel.setState("First")
            .andThen(viewModel.setState("Second"))
            .subscribe()

        viewModel.viewState.test()
            .assertValue("Second")
            .assertNotComplete()
    }

    test("runEffect - no subscribers - adds effect to queue") {
        val viewModel = JetpackViewModel<Nothing, String>()

        viewModel.runEffect("First")
            .andThen(viewModel.runEffect("Second"))
            .subscribe()

        viewModel.effects.test()
            .assertValues("First", "Second")
            .assertNotComplete()
    }

    test("runEffect - subscriber - consumes effect") {
        val viewModel = JetpackViewModel<Nothing, String>()
        val testObserver = viewModel.effects.test()

        viewModel.runEffect("First").subscribe()

        testObserver
            .assertValue("First")
            .dispose() // only one subscriber at a time

        viewModel.effects.test()
            .assertEmpty()
            .assertNotComplete()
    }
})
