package io.github.lordraydenmk.superheroesapp.superheroes

sealed class SuperheroError

data class NetworkError(val t: Throwable) : SuperheroError()
data class ServerError(val code: Int, val msg: String?) : SuperheroError()

data class SuperheroException(val error: SuperheroError) : Throwable() {

    override val cause: Throwable?
        get() = when (error) {
            is NetworkError -> error.t
            is ServerError -> null
        }

    override val message: String?
        get() = when (error) {
            is NetworkError -> "Network Error"
            is ServerError -> "Looks like the Marvel backend has issues"
        }

    // http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/
    override fun fillInStackTrace(): Throwable = this
}