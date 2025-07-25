package com.michaelflisar.toolbox.zip

import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import com.michaelflisar.toolbox.zip.interfaces.IZipFile

interface IZipManager<File: IZipFile, FileContentFile, FileContent: IZipContent<FileContentFile>> {
    suspend fun zip(files: List<FileContent>, zipFile: File): Result<Unit>
    suspend fun unzip(zipFile: File, files: List<FileContent>, replaceExistingFiles: Boolean = true): Result<Unit>
}