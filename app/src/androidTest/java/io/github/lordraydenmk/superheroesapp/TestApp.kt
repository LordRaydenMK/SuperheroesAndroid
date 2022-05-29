package io.github.lordraydenmk.superheroesapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import io.github.lordraydenmk.superheroesapp.utils.TestingModule
import io.github.lordraydenmk.superheroesapp.utils.testModule
import okhttp3.HttpUrl.Companion.toHttpUrl
import timber.log.Timber

class TestApp : Application(), ModuleOwner, ImageLoaderFactory {

    // appModule is now mutable so it can be replaced in tests
    override var appModule: TestingModule = testModule("https://localhost".toHttpUrl())

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader = FakeImageLoader
}