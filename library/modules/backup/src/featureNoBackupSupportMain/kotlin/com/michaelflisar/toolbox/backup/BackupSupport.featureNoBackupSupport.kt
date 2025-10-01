package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import io.github.vinceglb.filekit.PlatformFile

actual typealias ZipFileContentFile = NoZipFileContent.File
actual typealias ZipFileContent = NoZipFileContent

sealed class NoZipFileContent : IZipContent<NoZipFileContent.File> {
    class File(override val zipPath: String): NoZipFileContent() {
        override fun getSubFile(relativePath: String): File? {
            return null
        }
    }
}