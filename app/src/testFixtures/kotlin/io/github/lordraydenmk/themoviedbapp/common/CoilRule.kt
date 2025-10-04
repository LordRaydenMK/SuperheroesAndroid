package io.github.lordraydenmk.themoviedbapp.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import app.cash.paparazzi.Paparazzi
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.test.FakeImageLoaderEngine
import coil3.test.default

@OptIn(DelicateCoilApi::class)
fun setupCoil(paparazzi: Paparazzi) {
    val engine = FakeImageLoaderEngine.Builder()
        .default(ColorDrawable(Color.BLUE))
        .build()
    val imageLoader = ImageLoader.Builder(paparazzi.context)
        .components { add(engine) }
        .build()
    SingletonImageLoader.setUnsafe { imageLoader }
}
