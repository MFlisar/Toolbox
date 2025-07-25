package com.michaelflisar.toolbox.zip

object ZipUtil {

    const val PATH_DIVIDER = "/"

    fun cleanZipPath(path: String) = path.replace("\\", PATH_DIVIDER)

    fun isZipPathEqual(path: String, otherPath: String, ignoreCase: Boolean = true) =
        cleanZipPath(path).equals(cleanZipPath(otherPath), ignoreCase)

}
