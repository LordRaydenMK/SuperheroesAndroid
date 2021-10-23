package io.github.lordraydenmk.superheroesapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import coil.ImageLoader
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult

/**
 * FakeImageLoader that fills the image with black
 *
 * https://coil-kt.github.io/coil/image_loaders/#testing
 */
class FakeImageLoader : ImageLoader {

    private val disposable = object : Disposable {
        override val isDisposed get() = true
        override fun dispose() {}
        override suspend fun await() {}
    }

    override val defaults = DefaultRequestOptions()

    // Optionally, you can add a custom fake memory cache implementation.
    override val memoryCache get() = throw UnsupportedOperationException()

    override val bitmapPool = BitmapPool(0)

    override fun enqueue(request: ImageRequest): Disposable {
        // Always call onStart before onSuccess.
        request.target?.onStart(placeholder = ColorDrawable(Color.BLACK))
        request.target?.onSuccess(result = ColorDrawable(Color.BLACK))
        return disposable
    }

    override suspend fun execute(request: ImageRequest): ImageResult {
        return SuccessResult(
            drawable = ColorDrawable(Color.BLACK),
            request = request,
            metadata = ImageResult.Metadata(
                memoryCacheKey = MemoryCache.Key(""),
                isSampled = false,
                dataSource = DataSource.MEMORY_CACHE,
                isPlaceholderMemoryCacheKeyPresent = false
            )
        )
    }

    override fun shutdown() {}

    override fun newBuilder(): ImageLoader.Builder = throw UnsupportedOperationException()
}