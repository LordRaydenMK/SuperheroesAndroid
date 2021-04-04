package io.github.lordraydenmk.superheroesapp.utils

import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.TestApp
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit

fun testModule(baseUrl: HttpUrl): AppModule {

    val client = OkHttpClient.Builder()
        .apply { addInterceptor(HttpLoggingInterceptor().apply { setLevel(Level.BASIC) }) }
        .build()

    val idlingResource = OkHttp3IdlingResource.create("OkHttp", client)
    IdlingRegistry.getInstance().register(idlingResource)

    val contentType = "application/json".toMediaType()
    val converter = Json {
        ignoreUnknownKeys = true
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(converter.asConverterFactory(contentType))
        .client(client)
        .build()

    val service = retrofit.create(SuperheroesService::class.java)

    return object : AppModule, SuperheroesService by service {}
}

fun replaceAppModule(testModule: AppModule) {
    val testApp = getInstrumentation().targetContext.applicationContext as TestApp
    testApp.appModule = testModule
}