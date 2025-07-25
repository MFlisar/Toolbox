package com.michaelflisar.toolbox.zip

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun JavaZipManager.zipToCache(
    context: Context,
    files: List<JavaZipFileContent>
): File? {
    return withContext(Dispatchers.IO) {
        val targetZipFile = File.createTempFile("data_", ".zip", context.cacheDir)
        val result = zip(files, AndroidZipFile(Uri.fromFile(targetZipFile)))
        targetZipFile.takeIf { result.isSuccess }
    }
}