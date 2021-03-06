package io.github.lordraydenmk.superheroesapp

/**
 * Breaks the dependency between Fragments and Application
 *
 * Enables having a separate Application class in Espresso tests that implements this interface
 */
interface ModuleOwner {

    val appModule: AppModule
}