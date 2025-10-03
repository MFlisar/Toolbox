package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.zip.interfaces.IZipContent

expect class ActivityNotFoundException : RuntimeException

expect class ZipFileContentFile
expect sealed class ZipFileContent : IZipContent<ZipFileContentFile>

