package io.github.lordraydenmk.superheroesapp

import android.app.Application
import android.content.Context
import com.akaita.java.rxjava2debug.RxJava2Debug
import io.github.lordraydenmk.superheroesapp.common.CrashReportingTree
import timber.log.Timber

class App : Application(), ModuleOwner {

    override val appModule by lazy { AppModule.create() }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashReportingTree())

        // Enable RxJava assembly stack collection, to make RxJava crash reports clear and unique
        // Make sure this is called AFTER setting up any Crash reporting mechanism as Crashlytics
        // See https://github.com/akaita/RxJava2Debug for more
        RxJava2Debug.enableRxJava2AssemblyTracking()
    }
}

fun Context.appModule(): AppModule = (applicationContext as ModuleOwner).appModule