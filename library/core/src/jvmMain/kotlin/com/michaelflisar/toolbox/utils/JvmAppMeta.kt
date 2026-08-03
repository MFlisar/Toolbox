package com.michaelflisar.toolbox.utils

import androidx.compose.runtime.Stable
import java.io.File

@Stable
sealed class JvmAppMeta {

    enum class Type(
        val info: String,
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
        override val isDebug: Boolean,
    ) : JvmAppMeta() {
        override val isExe = false
        override val file = jarFile
    }

    @Stable
    data class Exe(
        val exeFile: File,
        override val isDebug: Boolean,
    ) : JvmAppMeta() {
        override val isExe = true
        override val file = exeFile
    }

    companion object {

        fun detect(
            cls: Class<*>,
            debug: Boolean,
            exe: Boolean,
        ): JvmAppMeta {
            val source = File(cls.protectionDomain.codeSource.location.toURI())
            return detect(source, debug, exe)
        }

        fun detect(
            sourceFile: File,
            debug: Boolean,
            exe: Boolean,
        ): JvmAppMeta {
            return when {
                exe ->
                    Exe(
                        exeFile = sourceFile,
                        isDebug = false
                    )

                !debug ->
                    Jar(
                        jarFile = sourceFile,
                        isDebug = false
                    )

                else ->
                    Jar(
                        jarFile = sourceFile,
                        isDebug = true
                    )
            }
        }
    }
}