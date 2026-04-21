package com.michaelflisar.toolbox.coil

import androidx.compose.runtime.Composable
import coil3.ComponentRegistry
import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.util.Logger
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level

object CoilManager {

    private var mayExistsFunction: (data: Any?) -> Boolean = { true }

    /**
     * init with custom fetchers (if needed)
     *
     * @param isDebug if true, logging is enabled (with advancedLogging)
     * @param advancedLogging if true, also log the messages from Coil (not only errors)
     * @param mayExist function to check if the given data may exist (e.g. file exists, url is valid, etc.) - default is always true
     *        damit kann man schnell prüfen, ob es sich überhaupt lohnt, einen Request zu starten
     * @builder components for custom fetchers (e.g. for platform specific data types)
     */
    @Composable
    fun Init(
        isDebug: Boolean,
        advancedLogging: Boolean = false,
        mayExist: (data: Any?) -> Boolean = { true },
        components: ComponentRegistry.Builder.() -> Unit = {},
    ) {
        mayExistsFunction = mayExist
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(false)
                .components(components)
                .apply {
                    if (isDebug) {
                        logger(object : Logger {

                            override var minLevel = Logger.Level.Verbose

                            override fun log(
                                tag: String,
                                level: Logger.Level,
                                message: String?,
                                throwable: Throwable?,
                            ) {
                                if (advancedLogging) {
                                    val lvl = when (level) {
                                        Logger.Level.Debug -> Level.DEBUG
                                        Logger.Level.Error -> Level.ERROR
                                        Logger.Level.Info -> Level.INFO
                                        Logger.Level.Verbose -> Level.VERBOSE
                                        Logger.Level.Warn -> Level.WARN
                                    }
                                    if (throwable != null) {
                                        L.tag("COIL").log(lvl, throwable) { "$tag: $message" }
                                    } else {
                                        L.tag("COIL").log(lvl) { "$tag: $message" }
                                    }
                                }
                            }
                        })
                    }
                }
                .build()
        }
    }

    suspend fun load(
        data: Any?,
        context: PlatformContext,
        builder: ImageRequest.Builder.() -> Unit = {},
    ): Image? {
        return if (mayExistsFunction(data)) {
            val request = ImageRequest.Builder(context)
                .data(data)
                .apply(builder)
                .build()
            val imageLoader = SingletonImageLoader.get(context)
            imageLoader.execute(request).image
        } else {
            null
        }
    }
}