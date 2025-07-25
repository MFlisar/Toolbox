package com.michaelflisar.toolbox.zip.interfaces

import kotlinx.io.Sink
import kotlinx.io.Source

/*
 * a zip file that can either be read or a folder at which a zip file can be unpacked to
 */
interface IZipFile {
    fun inputSource(source: (source: Source) -> Unit): Boolean
    fun outputSink(sink: (sink: Sink) -> Unit): Boolean
}