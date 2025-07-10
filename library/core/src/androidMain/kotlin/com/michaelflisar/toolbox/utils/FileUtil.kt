package com.michaelflisar.toolbox.utils

import com.michaelflisar.lumberjack.core.L
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.also

object FileUtil {

    fun createFile(file: File, content: String?) {
        if (content == null) {
            return
        }
        var out: BufferedWriter? = null
        var writer: FileWriter? = null
        try {
            file.delete()
            file.parentFile?.mkdirs()
            if (!file.exists()) {
                file.createNewFile()
            }
            writer = FileWriter(file)
            out = BufferedWriter(writer)
            out.write(content)
            out.close()
        } catch (e: IOException) {
            L.e(e)
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    // empty
                }
            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    // empty
                }
            }
        }
    }

    fun copyFile(openFrom: () -> InputStream, outFile: File): Boolean {
        var success: Boolean
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = openFrom()
            outFile.parentFile?.mkdirs()
            outFile.createNewFile()
            outputStream = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            success = true
        } catch (e: IOException) {
            L.e(e)
            success = false
        } catch (e: Exception) {
            L.e(e)
            success = false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    // NOOP
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    // NOOP
                }
            }
        }
        return success
    }

    fun saveStringAsFile(file: File?, content: String?, encoding: String? = null): Boolean {
        var encoding = encoding
        if (encoding == null) {
            encoding = "UTF-8"
        }
        var success = false
        var writer: BufferedWriter? = null
        try {
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), encoding))
            writer.write(content)
            success = true
        } catch (e: IOException) {
        } finally {
            try {
                writer?.close()
            } catch (e: IOException) {
            }
        }
        return success
    }
}