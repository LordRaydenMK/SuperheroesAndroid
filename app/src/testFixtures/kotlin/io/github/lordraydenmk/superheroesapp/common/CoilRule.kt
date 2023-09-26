package io.github.lordraydenmk.superheroesapp.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import app.cash.paparazzi.Paparazzi
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine

@ExperimentalCoilApi
fun setupCoil(paparazzi: Paparazzi) {
    val engine = FakeImageLoaderEngine.Builder()
        .default(ColorDrawable(Color.BLUE))
        .build()
    val imageLoader = ImageLoader.Builder(paparazzi.context)
        .components { add(engine) }
        .build()
    Coil.setImageLoader(imageLoader)
}
