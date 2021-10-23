package io.github.lordraydenmk.superheroesapp

import coil.ImageLoader
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.superheroesapp.common.md5
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import kotlinx.serialization.ExperimentalSerializationApi
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
interface AppModule : SuperheroesService, ImageLoader {

private val empty: (Any) -> Unit = { }

interface AppModule : SuperheroesService {

    val afterBind: (Any) -> Unit
        get() = empty

    companion object {

        private val authInterceptor = { chain: Interceptor.Chain ->
            val ts = System.currentTimeMillis()

            val hash =
                "$ts${BuildConfig.MARVEL_PRIVATE_API_KEY}${BuildConfig.MARVEL_PUBLIC_API_KEY}".md5()
            val request = chain.request()
            val url = request.url
                .newBuilder()
                .addQueryParameter("ts", ts.toString())
                .addQueryParameter("apikey", BuildConfig.MARVEL_PUBLIC_API_KEY)
                .addQueryParameter("hash", hash)
                .build()
            val updated = request.newBuilder()
                .url(url)
                .build()

            chain.proceed(updated)
        }

        private val userAgentInterceptor = { chain: Interceptor.Chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("User-Agent", "Superheroes app/${BuildConfig.VERSION_CODE}")
                    .build()
            )
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(userAgentInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply { setLevel(Level.BASIC) })
                }
            }
            .build()

        private val contentType = "application/json".toMediaType()
        private val converter = Json {
            ignoreUnknownKeys = true
        }

        @OptIn(ExperimentalSerializationApi::class)
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://gateway.marvel.com/v1/public/")
            .addConverterFactory(converter.asConverterFactory(contentType))
            .client(client)
            .build()

        private val superheroesService = retrofit.create(SuperheroesService::class.java)

        fun create(
            imageLoader: ImageLoader,
            superheroesService: SuperheroesService = this.superheroesService
        ): AppModule =
            object : AppModule, ImageLoader by imageLoader,
                SuperheroesService by superheroesService {}
    }
}