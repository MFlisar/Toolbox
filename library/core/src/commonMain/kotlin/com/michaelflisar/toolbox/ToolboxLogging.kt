package com.michaelflisar.toolbox

import com.michaelflisar.lumberjack.core.L

object ToolboxLogging {

    enum class Tag {
        None,
        Service,
        Navigation,
        AppUpdate,
        UpdateMananger,
        Window
    }

    private var enabledTags: List<Tag> = emptyList()

    fun enableAll(excluded: List<Tag> = emptyList()) {
        enabledTags = Tag.entries.toList().filter { it !in excluded}
    }

    fun enable(vararg tags: Tag) {
        enabledTags = tags.toList()
    }

    internal fun isEnabled(tag: Tag): Boolean {
        return enabledTags.isNotEmpty() && enabledTags.contains(tag)
    }
}

fun L.logIf(tag: ToolboxLogging.Tag, force: Boolean = false) =
    logIf { !force && ToolboxLogging.isEnabled(tag) }?.tag(tag.name)