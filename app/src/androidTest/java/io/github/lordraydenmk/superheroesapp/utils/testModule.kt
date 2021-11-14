package io.github.lordraydenmk.superheroesapp.utils

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.TestApp
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import timber.log.Timber

fun testModule(baseUrl: HttpUrl): TestingModule {

    val client = OkHttpClient.Builder()
//        .apply { addInterceptor(HttpLoggingInterceptor().apply { setLevel(Level.BASIC) }) }
        .build()

//    val idlingResource = OkHttp3IdlingResource.create("OkHttp", client)
//    IdlingRegistry.getInstance().register(idlingResource)

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

    return object : TestingModule, SuperheroesService by service {

        override var state: Any = Unit

        var count = 0

        override var afterBind: (Any) -> Unit = {
            state = it
            Timber.d("afterBind ${count++} state=$it")
        }
    }
}

fun replaceAppModule(testModule: AppModule) {
    val testApp = getInstrumentation().targetContext.applicationContext as TestApp
    testApp.appModule = testModule
}