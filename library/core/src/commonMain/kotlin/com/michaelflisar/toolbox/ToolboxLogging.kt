package com.michaelflisar.toolbox

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level

object ToolboxLogging {

    enum class Tag {
        None,
        Service,
        Navigation,
        AppUpdate,
        UpdateMananger,
        Window,
        Coil,
        CoilError
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
        return enabledLevel <= level
    }
}

fun L.info(tag: ToolboxLogging.Tag, force: Boolean = false, message: () -> String) =
    logIf { !force && ToolboxLogging.isEnabled(tag, Level.INFO) }?.tag(tag.name)?.i(message)

fun L.info(
    tag: ToolboxLogging.Tag,
    force: Boolean = false,
    t: Throwable,
    message: (() -> String)? = null,
) =
    logIf { !force && ToolboxLogging.isEnabled(tag, Level.INFO) }?.tag(tag.name)?.let {
        if (message != null) it.i(t, message) else it.i(t)
    }

fun L.debug(tag: ToolboxLogging.Tag, force: Boolean = false, message: () -> String) =
    logIf { !force && ToolboxLogging.isEnabled(tag, Level.DEBUG) }?.tag(tag.name)?.d(message)

fun L.error(tag: ToolboxLogging.Tag, force: Boolean = false, message: () -> String) =
    logIf { !force && ToolboxLogging.isEnabled(tag, Level.ERROR) }?.tag(tag.name)?.e(message)

fun L.error(
    tag: ToolboxLogging.Tag,
    force: Boolean = false,
    t: Throwable,
    message: (() -> String)? = null,
) =
    logIf { !force && ToolboxLogging.isEnabled(tag, Level.ERROR) }?.tag(tag.name)?.let {
        if (message != null) it.e(t, message) else it.e(t)
    }