package io.github.lordraydenmk.superheroes.app

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.superheroesapp.AppModule
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

fun AppModule.Companion.create(
    service: SuperheroesService = buildService()
): AppModule = object : AppModule, SuperheroesService by service {}

private fun buildService(): SuperheroesService {
    val authInterceptor = { chain: Interceptor.Chain ->
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

    val userAgentInterceptor = { chain: Interceptor.Chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader("User-Agent", "Superheroes app/${BuildConfig.VERSION_CODE}")
                .build()
        )
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(userAgentInterceptor)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { setLevel(Level.BASIC) })
            }
        }
        .build()

    val contentType = "application/json".toMediaType()
    val converter = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://gateway.marvel.com/v1/public/")
        .addConverterFactory(converter.asConverterFactory(contentType))
        .client(client)
        .build()

    return retrofit.create(SuperheroesService::class.java)
}