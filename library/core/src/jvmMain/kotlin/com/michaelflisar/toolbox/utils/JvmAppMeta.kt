package com.michaelflisar.toolbox.utils

import androidx.compose.runtime.Stable
import java.io.File

@Stable
sealed class JvmAppMeta {

    enum class Type(
        val info: String
    ) {
        DebugExe("Debug Exe"),
        ReleaseExe("Release Exe"),
        DebugJar("Debug Jar"),
        ReleaseJar("Release Jar")
    }

    abstract val isDebug: Boolean
    abstract val isExe: Boolean

    abstract val file: File

    fun getType(): Type {
        return when (this) {
            is Exe -> if (isDebug) Type.DebugExe else Type.ReleaseExe
            is Jar -> if (isDebug) Type.DebugJar else Type.ReleaseJar
        }
    }

    @Stable
    data class Jar(
        val jarFile: File,
        override val isDebug: Boolean
    ) : JvmAppMeta() {
        override val isExe = false
        override val file = jarFile
    }

    @Stable
    data class Exe(
        val exeFile: File,
        override val isDebug: Boolean
    ) : JvmAppMeta() {
        override val isExe = true
        override val file = exeFile
    }

    companion object {

        fun detect(
            cls: Class<*>,
            isReleaseDistributable: (path: String) -> Boolean = {
                Regex(""".*/build/compose/binaries/.+-release/.*""").matches(it)
            },
            isDebug: (path: String) -> Boolean = {
                Regex(""".*/build/compose/binaries/.*""").matches(it)
            }
        ): JvmAppMeta {
            val source = File(
                cls.protectionDomain.codeSource.location.toURI()
            )

            val path = source.invariantSeparatorsPath

            val isReleaseDistributable = isReleaseDistributable(path)
            val isDebugDistributable = isDebug(path)
            val isExe = source.extension.equals("exe", ignoreCase = true)

            return when {

                isExe ->
                    Exe(
                        exeFile = source,
                        isDebug = false
                    )

                isReleaseDistributable ->
                    Jar(
                        jarFile = source,
                        isDebug = false
                    )

                isDebugDistributable ->
                    Jar(
                        jarFile = source,
                        isDebug = true
                    )

                else ->
                    Jar(
                        jarFile = source,
                        isDebug = true
                    )
            }
        }
    }
}