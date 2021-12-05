package io.github.lordraydenmk.superheroesapp.utils

import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.lordraydenmk.superheroesapp.TestApp
import io.github.lordraydenmk.superheroesapp.superheroes.data.SuperheroesService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import timber.log.Timber

fun testModule(baseUrl: HttpUrl): TestingModule {

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

    return object : TestingModule, SuperheroesService by service {

        private val state = MutableStateFlow<Any>(Unit)

        var count = 0

        override val afterBind: (Any) -> Unit = {
            state.value = it
            Timber.d("afterBind ${count++} state=$it")
        }

        override suspend fun <A : Any> awaitState(clazz: Class<A>) {
            try {
                withTimeout(3_000) {
                    state.mapNotNull { if (clazz.isAssignableFrom(it.javaClass)) clazz.cast(it) else null }
                        .first()
                }
            } catch (e: TimeoutCancellationException) {
                Timber.d("Expected state to be type: ${clazz::class.java} found value: ${state.value}")
                throw e
            }
        }
    }
}

suspend inline fun <reified T : Any> TestingModule.awaitState() = awaitState(T::class.java)

fun testModule(): TestingModule =
    (getInstrumentation().targetContext.applicationContext as TestApp).appModule

fun replaceAppModule(testModule: TestingModule) {
    val testApp = getInstrumentation().targetContext.applicationContext as TestApp
    testApp.appModule = testModule
}