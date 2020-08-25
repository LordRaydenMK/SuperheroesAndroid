package io.github.lordraydenmk.superheroesapp

import android.app.Application
import android.content.Context
import com.akaita.java.rxjava2debug.RxJava2Debug
import timber.log.Timber

class App : Application() {

    val appModule by lazy { AppModule.create() }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        // Enable RxJava assembly stack collection, to make RxJava crash reports clear and unique
        // Make sure this is called AFTER setting up any Crash reporting mechanism as Crashlytics
        RxJava2Debug.enableRxJava2AssemblyTracking()
    }
}

fun Context.appModule(): AppModule = (applicationContext as App).appModule