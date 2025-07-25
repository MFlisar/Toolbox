package com.michaelflisar.toolbox.zip

import com.michaelflisar.toolbox.zip.interfaces.IZipFile
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface IJavaZipFile : IZipFile {
    fun openInputStream(): InputStream?
    fun openOutputStream(): OutputStream?

    override fun inputSource(source: (source: Source) -> Unit): Boolean {
        var success = false
        openInputStream().use { inputStream ->
            inputStream?.asSource()?.buffered()?.use {
                source(it)
                success = true
            }
        }
        return success
    }

    override fun outputSink(sink: (sink: Sink) -> Unit): Boolean {
        var success = false
        openOutputStream().use { outputStream ->
            outputStream?.asSink()?.buffered()?.use {
                sink(it)
                success = true
            }
        }
        return success
    }
}

@Serializable(with = JavaZipFile.Companion.ZipFileSerializer::class)
class JavaZipFile(
    val file: File,
) : IJavaZipFile {

    companion object {
        object ZipFileSerializer : KSerializer<JavaZipFile> {

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("ZipFile", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: JavaZipFile) {
                encoder.encodeString(value.file.absolutePath)
            }

            override fun deserialize(decoder: Decoder): JavaZipFile {
                return JavaZipFile(File(decoder.decodeString()))
            }
        }
    }

    override fun openInputStream() = file.inputStream()
    override fun openOutputStream() = file.outputStream()

}