package com.michaelflisar.toolbox.zip.interfaces

import kotlinx.io.Sink
import kotlinx.io.Source

/*
 * a file/folder that can be put into a zip file and defines all the relevant informations for this purpose
 */
interface IZipContent<File> {

    val zipPath: String

    fun getSubFile(relativePath: String): File?

    interface File<F: File<F>> : IZipContent<F> {
        fun inputSource(source: (source: Source) -> Unit) : Boolean
        fun outputSink(sink: (sink: Sink) -> Unit) : Boolean
        fun mkParentDirs() : Boolean
        fun delete() : Boolean
    }

    interface Folder<F: File<F>> : IZipContent<F> {
        val exclude: (relativePath: String) -> Boolean
        fun listFiles(): List<F>
    }
}