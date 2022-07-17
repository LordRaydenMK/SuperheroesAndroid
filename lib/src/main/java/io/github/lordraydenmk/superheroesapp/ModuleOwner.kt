package io.github.lordraydenmk.superheroesapp

import android.content.Context

/**
 * Breaks the dependency between Fragments and Application
 *
 * Enables having a separate Application class in Espresso tests that implements this interface
 */
interface ModuleOwner {

    val appModule: AppModule
}

fun Context.appModule(): AppModule = (applicationContext as ModuleOwner).appModule