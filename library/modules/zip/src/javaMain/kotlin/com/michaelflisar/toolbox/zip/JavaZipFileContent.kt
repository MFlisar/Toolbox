package com.michaelflisar.toolbox.zip

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

@Serializable
sealed class JavaZipFileContent : IZipContent<JavaZipFileContent.File> {

    @Serializable(with = File.Companion.ZipContentFileSerializer::class)
    class File(
        val file: java.io.File,
        override val zipPath: String,
    ) : JavaZipFileContent(), IZipContent.File<File> {

        companion object {
            object ZipContentFileSerializer : KSerializer<File> {

                override val descriptor: SerialDescriptor =
                    PrimitiveSerialDescriptor("ZipFileContent::File", PrimitiveKind.STRING)

                override fun serialize(encoder: Encoder, value: File) {
                    encoder.encodeSerializableValue(
                        ListSerializer(String.serializer()),
                        listOf(value.file.absolutePath, value.zipPath)
                    )
                }

                override fun deserialize(decoder: Decoder): File {
                    val (filePath, zipPath) = decoder.decodeSerializableValue(ListSerializer(String.serializer()))
                    return File(File(filePath), zipPath)
                }
            }
        }

        fun openInputStream() = file.inputStream()
        fun openOutputStream() = file.outputStream()

        override fun inputSource(source: (source: Source) -> Unit): Boolean {
            var success = false
            try {
                file.inputStream().use { inputStream ->
                    val source = inputStream.asSource().buffered()
                    source(source)
                    source.close()
                    success = true
                }
            } catch (e: Exception) {
                L.d(e)
            }
            return success
        }

        override fun outputSink(sink: (sink: Sink) -> Unit): Boolean {
            var success = false
            try {
                file.outputStream().use { outputStream ->
                    val sink = outputStream.asSink().buffered()
                    sink(sink)
                    sink.close()
                    success = true
                }
            } catch (e: Exception) {
                L.d(e)
            }
            return success
        }

        override fun mkParentDirs(): Boolean {
            return file.parentFile?.mkdirs() ?: false
        }

        override fun delete(): Boolean {
            return file.delete()
        }

        override fun getSubFile(relativePath: String): File? {
            if (ZipUtil.isZipPathEqual(zipPath, relativePath)) {
                return this
            }
            return null
        }
    }

    @Serializable(with = Folder.Companion.ZipContentFolderSerializer::class)
    class Folder(
        val folder: java.io.File,
        override val zipPath: String,
        override val exclude: (relativePath: String) -> Boolean = { false },
    ) : JavaZipFileContent(), IZipContent.Folder<File> {

        companion object {
            object ZipContentFolderSerializer : KSerializer<Folder> {

                override val descriptor: SerialDescriptor =
                    PrimitiveSerialDescriptor("ZipContent::Folder", PrimitiveKind.STRING)

                override fun serialize(encoder: Encoder, value: Folder) {
                    encoder.encodeSerializableValue(
                        ListSerializer(String.serializer()),
                        listOf(value.folder.absolutePath, value.zipPath)
                    )
                }

                override fun deserialize(decoder: Decoder): Folder {
                    val (filePath, zipPath) = decoder.decodeSerializableValue(ListSerializer(String.serializer()))
                    return Folder(File(filePath), zipPath)
                }
            }
        }

        override fun getSubFile(relativePath: String): File? {
            val cleanPath = ZipUtil.cleanZipPath(relativePath)
            val cleanZipPath = ZipUtil.cleanZipPath(zipPath)
            return if (cleanPath.startsWith(cleanZipPath + ZipUtil.PATH_DIVIDER)) {
                val file = folder.resolve(cleanPath.drop(cleanZipPath.length + 1))
                val zipPath =
                    cleanPath + ZipUtil.PATH_DIVIDER + cleanPath.drop(cleanZipPath.length + 1)
                File(file, zipPath)
            } else null
        }

        override fun listFiles(): List<File> {
            return folder
                .walkTopDown()
                .toList()
                .filter { it.isFile }
                .mapNotNull {
                    val relativePath = it.relativeTo(folder).path
                    val relativePathCleaned = ZipUtil.cleanZipPath(relativePath)
                    if (exclude(relativePathCleaned)) {
                        null
                    } else {
                        File(
                            it,
                            ZipUtil.cleanZipPath(zipPath) + ZipUtil.PATH_DIVIDER + relativePathCleaned
                        )
                    }
                }
        }
    }

}

