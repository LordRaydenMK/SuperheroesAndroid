package io.github.lordraydenmk.themoviedbapp

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import timber.log.Timber

class App : Application(), SingletonImageLoader.Factory {

    val appModule by lazy { AppModule.create() }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader(context).newBuilder()
            .crossfade(true)
            .build()
}

fun Context.appModule(): AppModule = (applicationContext as App).appModule