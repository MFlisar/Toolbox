package com.michaelflisar.toolbox.app.classes

sealed class Developer {

    abstract val name: String
    abstract val email: String

    class Author(override val name: String, override val email: String) : Developer()
    class Company(override val name: String, override val email: String) : Developer()

}