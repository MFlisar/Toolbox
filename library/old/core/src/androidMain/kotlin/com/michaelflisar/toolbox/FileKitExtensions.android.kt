package com.michaelflisar.toolbox

import android.net.Uri
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.fromBookmarkData

fun PlatformFile.toAndroidUriInternal(): Uri = when (val androidFile = androidFile) {
    is AndroidFile.UriWrapper -> androidFile.uri
    is AndroidFile.FileWrapper -> Uri.fromFile(androidFile.file)
}