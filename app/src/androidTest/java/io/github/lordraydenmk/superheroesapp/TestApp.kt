package io.github.lordraydenmk.superheroesapp

import android.app.Application
import timber.log.Timber

class TestApp : Application(), ModuleOwner {

    // appModule is now mutable so it can be replaced in tests
    override var appModule: AppModule = AppModule.create()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}