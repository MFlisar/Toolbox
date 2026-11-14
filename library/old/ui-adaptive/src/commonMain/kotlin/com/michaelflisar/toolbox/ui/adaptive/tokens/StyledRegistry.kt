package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
class StyledKey<T : Any>(
    val name: String,
) {
    override fun toString(): String = "StyledKey($name)"
}

@Stable
class StyledRegistry(
    private val parent: StyledRegistry? = null,
    private val entries: MutableMap<StyledKey<*>, Any> = mutableStateMapOf(),
) {
    companion object {

        fun create(
            parent: StyledRegistry? = null,
            builder: StyledRegistry.Builder.() -> Unit,
        ): StyledRegistry {
            val componentsBuilder = StyledRegistry.Builder()
            componentsBuilder.builder()
            val entries = componentsBuilder.pairs()
            return StyledRegistry(
                parent = parent,
                entries = mutableStateMapOf(*entries)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrNull(key: StyledKey<T>): T? =
        (entries[key] as? T) ?: parent?.getOrNull(key)

    operator fun <T : Any> get(key: StyledKey<T>): T =
        getOrNull(key)
            ?: error("No implementation registered for ${key.name}")

    class Builder internal constructor() {

        private val entries = mutableMapOf<StyledKey<*>, Any>()

        fun <T : Any> register(
            key: StyledKey<T>,
            implementation: T,
        ) {
            entries[key] = implementation
        }

        internal fun pairs() = entries.toList().toTypedArray()
    }
}

internal val LocalStyledRegistry = staticCompositionLocalOf { StyledRegistry() }

@Composable
fun UpdateStyledRegistry(
    update: @Composable StyledRegistry.Builder.() -> Unit,
    content: @Composable () -> Unit,
) {
    val parentRegistry = LocalStyledRegistry.current

    val componentsBuilder = StyledRegistry.Builder()
    componentsBuilder.update()
    val entries = componentsBuilder.pairs()
    val childRegistry = remember(parentRegistry, entries) {
        StyledRegistry(
            parent = parentRegistry,
            entries = mutableStateMapOf(*entries)
        )
    }

    CompositionLocalProvider(
        LocalStyledRegistry provides childRegistry,
    ) {
        content()
    }
}