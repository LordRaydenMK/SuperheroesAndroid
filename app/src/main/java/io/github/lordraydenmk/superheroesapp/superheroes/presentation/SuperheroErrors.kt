package io.github.lordraydenmk.superheroesapp.superheroes.presentation

sealed class SuperheroError

sealed class Recoverable : SuperheroError()
data class NetworkError(val t: Throwable) : Recoverable()
data class ServerError(val code: Int, val msg: String?) : Recoverable()

data class Unrecoverable(val t: Throwable) : SuperheroError()

data class SuperheroException(val error: SuperheroError) : Throwable() {

    override val cause: Throwable?
        get() = when (error) {
            is NetworkError -> error.t
            is ServerError -> null
            is Unrecoverable -> error.t
        }

    override val message: String?
        get() = when (error) {
            is NetworkError -> "Network Error"
            is ServerError -> "Looks like the Marvel backend has issues"
            is Unrecoverable -> "Logic bug!"
        }

    // http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/
    override fun fillInStackTrace(): Throwable = this
}