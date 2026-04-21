package com.michaelflisar.toolbox.app.classes

import com.michaelflisar.toolbox.app.Constants

sealed class Developer {

    companion object {
        val MFLISAR = Author(Constants.DEVELOPER_NAME, Constants.DEVELOPER_EMAIL)
    }

    abstract val name: String
    abstract val email: String

    class Author(override val name: String, override val email: String) : Developer()
    class Company(override val name: String, override val email: String) : Developer()

}