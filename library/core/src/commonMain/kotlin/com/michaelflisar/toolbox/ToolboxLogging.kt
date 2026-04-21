package com.michaelflisar.toolbox

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level

object ToolboxLogging {

    enum class Tag {
        None,
        Service,
        Navigation,
        AppUpdate,
        UpdateManager,
        Window,
        Coil
    }

    private var enabledTags: Map<Tag, Level> = HashMap()

    fun enableAll(excluded: List<Tag> = emptyList(), level: Level = Level.VERBOSE) {
        enabledTags = Tag.entries.toList()
            .filter { it !in excluded }
            .associateWith { level }
    }

    fun enable(level: Level = Level.VERBOSE, vararg tags: Tag) {
        enabledTags = tags.toList().associateWith { level }
    }

    internal fun isEnabled(tag: Tag, level: Level): Boolean {
        val enabledLevel = enabledTags[tag] ?: return false
        return enabledLevel.order <= level.order
    }
}

private const val callStackCorrection = 3

private fun L.log(
    level: Level,
    tag: ToolboxLogging.Tag,
    force: Boolean = false,
    t: Throwable? = null,
    message: (() -> String)?,
) {
    logIf { !force && ToolboxLogging.isEnabled(tag, level) }
        ?.tag(tag.name)
        ?.callStackCorrection(callStackCorrection)
        ?.let {
            if (t == null) {
                if (message != null)
                    it.log(level, message)
                else
                    // passiert nie, wenn t == null!
                    throw RuntimeException("ToolboxLogging: No message provided for log call with tag $tag and level $level")
            } else {
                if (message != null)
                    it.log(level, t, message)
                else
                    it.log(level, t)
            }
        }
}

fun L.info(tag: ToolboxLogging.Tag, force: Boolean = false, message: () -> String) =
    log(Level.INFO, tag, force, message = message)

fun L.info(
    tag: ToolboxLogging.Tag,
    force: Boolean = false,
    t: Throwable,
    message: (() -> String)? = null,
) = log(Level.INFO, tag, force, t, message ?: { "" })

fun L.debug(tag: ToolboxLogging.Tag, force: Boolean = false, message: () -> String) =
    log(Level.DEBUG, tag, force, message = message)

fun L.debug(
    tag: ToolboxLogging.Tag,
    force: Boolean = false,
    t: Throwable,
    message: (() -> String)? = null,
) = log(Level.DEBUG, tag, force, t, message ?: { "" })

fun L.error(tag: ToolboxLogging.Tag, force: Boolean = false, message: () -> String) =
    log(Level.ERROR, tag, force, message = message)

fun L.error(
    tag: ToolboxLogging.Tag,
    force: Boolean = false,
    t: Throwable,
    message: (() -> String)? = null,
) = log(Level.ERROR, tag, force, t, message ?: { "" })