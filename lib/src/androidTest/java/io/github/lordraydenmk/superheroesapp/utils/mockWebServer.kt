package io.github.lordraydenmk.superheroesapp.utils

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.InputStreamReader

fun readFromAssets(fileName: String): String =
    InputStreamReader(getInstrumentation().targetContext.assets.open(fileName))
        .use { inputStreamReader ->
            inputStreamReader.buffered().readText()
        }


fun MockWebServer.enqueueJsonFromAssets(fileName: String) {
    val jsonString = readFromAssets(fileName)
    enqueue(MockResponse().setBody(jsonString))
}