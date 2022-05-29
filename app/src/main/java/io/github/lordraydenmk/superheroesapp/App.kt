package io.github.lordraydenmk.superheroesapp

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import io.github.lordraydenmk.superheroesapp.common.CrashReportingTree
import timber.log.Timber

class App : Application(), ModuleOwner, ImageLoaderFactory {

    override val appModule by lazy { AppModule.create() }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashReportingTree())
    }

    override fun newImageLoader(): ImageLoader = ImageLoader(this)
}

fun Context.appModule(): AppModule = (applicationContext as ModuleOwner).appModule