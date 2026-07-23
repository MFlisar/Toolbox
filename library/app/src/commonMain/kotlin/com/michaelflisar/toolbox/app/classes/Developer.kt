package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Stable
import com.michaelflisar.toolbox.app.Constants

@Stable
sealed class Developer {

    companion object {
        val MFLISAR = Author(Constants.DEVELOPER_NAME, Constants.DEVELOPER_EMAIL)
    }

    abstract val name: String
    abstract val email: String

    @Stable
    class Author(override val name: String, override val email: String) : Developer()

    @Stable
    class Company(override val name: String, override val email: String) : Developer()

}