package io.github.lordraydenmk.superheroesapp.common

import coil.ComponentRegistry
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult

object FakeImageLoader : ImageLoader {

    override val components: ComponentRegistry = ComponentRegistry()

    override val defaults: DefaultRequestOptions = DefaultRequestOptions()

    override val diskCache: DiskCache? = null

    override val memoryCache: MemoryCache
        get() = throw UnsupportedOperationException()

    override fun enqueue(request: ImageRequest): Disposable = throw UnsupportedOperationException()

    override suspend fun execute(request: ImageRequest): ImageResult =
        throw UnsupportedOperationException()

    override fun newBuilder(): ImageLoader.Builder = throw UnsupportedOperationException()

    override fun shutdown() = throw UnsupportedOperationException()
}