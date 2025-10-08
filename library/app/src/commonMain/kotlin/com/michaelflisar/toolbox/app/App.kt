package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import cafe.adriel.voyager.core.platform.multiplatformName
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.app.features.logging.LogManager

object App {

    private val singletons: HashMap<String, Any> = HashMap()

    internal fun init(
        setup: AppSetup,
    ) {
        registerSingleton(setup)

        // logging
        LogManager.init()

        // themes
        ComposeTheme.register(*setup.themeSupport.themes.toTypedArray())

        // changelog
        Changelog.registerRenderer(
            ChangelogHeaderRenderer {
                val icon = when (it?.lowercase()) {
                    "info" -> Icons.Default.Info
                    "new" -> Icons.Default.NewReleases
                    "warning" -> Icons.Default.Warning
                    else -> null
                }
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )
    }

    fun getSingleton(key: String) = singletons[key]
    fun containsSingleton(key: String) = singletons.contains(key)
    fun addSingleton(key: String, instance: Any) {
        singletons.put(key, instance)
    }

    // extension gibt es bereits in voyager...
    inline fun <reified T> getKey(): String = T::class.multiplatformName!!

    inline fun <reified T> registerSingleton(instance: T) {
        val key = getKey<T>()
        if (containsSingleton(key)) {
            throw IllegalArgumentException("Singleton with key $key is already registered!")
        }
        addSingleton(key, instance as Any)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getSingleton(): T? {
        return getSingleton(getKey<T>())?.let { it as T }
    }

    inline fun <reified T> requireSingleton(): T {
        return getSingleton<T>()
            ?: throw IllegalArgumentException("Singleton with key ${getKey<T>()} not found!")
    }

}