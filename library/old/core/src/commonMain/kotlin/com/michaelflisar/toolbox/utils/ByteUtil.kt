package com.michaelflisar.toolbox.utils

import com.michaelflisar.toolbox.extensions.toRoundedString

internal object ByteUtil {

    private const val BYTE = 1L
    private const val KiB = BYTE shl 10
    private const val MiB = KiB shl 10
    private const val GiB = MiB shl 10
    private const val TiB = GiB shl 10
    private const val PiB = TiB shl 10
    private const val EiB = PiB shl 10

    private const val KB = BYTE * 1000
    private const val MB = KB * 1000
    private const val GB = MB * 1000
    private const val TB = GB * 1000
    private const val PB = TB * 1000
    private const val EB = PB * 1000

    private fun formatSize(
        size: Long,
        divider: Long,
        unitName: String,
        commas: Int
    ): String {
        val number = size.toDouble() / divider
        return number.toRoundedString(commas) + " " + unitName
    }

    fun toHumanReadableBinaryPrefixes(size: Long, commas: Int): String {
        require(size >= 0) { "Invalid file size: $size" }
        if (size >= EiB) return formatSize(size, EiB, "EiB", commas)
        if (size >= PiB) return formatSize(size, PiB, "PiB", commas)
        if (size >= TiB) return formatSize(size, TiB, "TiB", commas)
        if (size >= GiB) return formatSize(size, GiB, "GiB", commas)
        if (size >= MiB) return formatSize(size, MiB, "MiB", commas)
        return if (size >= KiB) formatSize(size, KiB, "KiB", commas) else formatSize(
            size,
            BYTE,
            "Bytes",
            commas
        )
    }

    fun toHumanReadableSIPrefixes(size: Long, commas: Int): String {
        require(size >= 0) { "Invalid file size: $size" }
        if (size >= EB) return formatSize(size, EB, "EB", commas)
        if (size >= PB) return formatSize(size, PB, "PB", commas)
        if (size >= TB) return formatSize(size, TB, "TB", commas)
        if (size >= GB) return formatSize(size, GB, "GB", commas)
        if (size >= MB) return formatSize(size, MB, "MB", commas)
        return if (size >= KB) formatSize(size, KB, "KB", commas) else formatSize(
            size,
            BYTE,
            "Bytes",
            commas
        )
    }
}