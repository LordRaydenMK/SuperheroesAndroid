package io.github.lordraydenmk.superheroesapp.utils

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.superheroesapp.TestApp
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
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

        private val _state = MutableStateFlow<Any>(Unit)
        override val state: StateFlow<Any> = _state.asStateFlow()

        var count = 0

        override val afterBind: (Any) -> Unit = {
            _state.value = it
            Timber.d("afterBind ${count++} state=$it")
        }
    }
}

suspend inline fun <reified T> TestingModule.awaitState() {
    try {
        withTimeout(1_000) {
            state.filterIsInstance<T>().first()
        }
    } catch (e: TimeoutCancellationException) {
        Timber.d("Expected state to be type: ${T::class.java} found value: ${state.value}")
        throw e
    }
}

fun testModule(): TestingModule =
    (getInstrumentation().targetContext.applicationContext as TestApp).appModule

fun replaceAppModule(testModule: TestingModule) {
    val testApp = getInstrumentation().targetContext.applicationContext as TestApp
    testApp.appModule = testModule
}