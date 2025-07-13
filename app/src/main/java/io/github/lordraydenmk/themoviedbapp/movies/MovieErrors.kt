package io.github.lordraydenmk.themoviedbapp.movies

sealed class MovieError

data class NetworkError(val t: Throwable) : MovieError()
data class ServerError(val code: Int, val msg: String?) : MovieError()

data class MovieException(val error: MovieError) : Throwable() {

    override val cause: Throwable?
        get() = when (error) {
            is NetworkError -> error.t
            is ServerError -> null
        }

    override val message: String?
        get() = when (error) {
            is NetworkError -> "Network Error"
            is ServerError -> "Looks like The Movie DB backend has issues"
        }

    // http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/
    override fun fillInStackTrace(): Throwable = this
}