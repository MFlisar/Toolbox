package com.michaelflisar.toolbox.zip

import android.net.Uri
import com.michaelflisar.toolbox.AppContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AndroidZipFile.Companion.ZipFileSerializer::class)
class AndroidZipFile(
    val uri: Uri,
) : IJavaZipFile {

    companion object {
        object ZipFileSerializer : KSerializer<AndroidZipFile> {

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("ZipFile", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: AndroidZipFile) {
                encoder.encodeString(value.uri.toString())
            }

            override fun deserialize(decoder: Decoder): AndroidZipFile {
                return AndroidZipFile(Uri.parse(decoder.decodeString()))
            }
        }
    }

    override fun openInputStream() = AppContext.context().contentResolver.openInputStream(uri)
    override fun openOutputStream() = AppContext.context().contentResolver.openOutputStream(uri)

}