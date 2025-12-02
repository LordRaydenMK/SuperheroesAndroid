package io.github.lordraydenmk.themoviedbapp

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.themoviedbapp.movies.data.TheMovieDbService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit

/**
 * Contains dependencies with Singleton/App scope
 */
interface AppModule : TheMovieDbService {

    companion object {

        private val authInterceptor = { chain: Interceptor.Chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
                .build()

            chain.proceed(request)
        }

        private val userAgentInterceptor = { chain: Interceptor.Chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("User-Agent", "Movies App/${BuildConfig.VERSION_CODE}")
                    .build()
            )
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(userAgentInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply { setLevel(Level.BODY) })
                }
            }
            .build()

        private val contentType = "application/json".toMediaType()
        private val converter = Json {
            ignoreUnknownKeys = true
        }

        private val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .addConverterFactory(converter.asConverterFactory(contentType))
            .client(client)
            .build()

        private val theMovieDbService = retrofit.create(TheMovieDbService::class.java)

        fun create(
            theMovieDbService: TheMovieDbService = this.theMovieDbService
        ): AppModule =
            object : AppModule, TheMovieDbService by theMovieDbService {}
    }
}