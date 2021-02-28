package io.github.lordraydenmk.superheroesapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Replaces the [Application] with [TestApp] which supports replacing the AppModule for tests
 */
class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application = super.newApplication(cl, TestApp::class.java.name, context)
}