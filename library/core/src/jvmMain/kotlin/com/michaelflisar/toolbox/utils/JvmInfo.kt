package com.michaelflisar.toolbox.utils

import java.net.InetAddress

data class JvmInfo(
    val label: String,
    private val supplier: () -> String,
) {
    val value: String
        get() = supplier()

    companion object {
        val JavaHome = JvmInfo("Java Home") {
            System.getProperty("java.home")
        }

        val JavaJRE = JvmInfo("Java Runtime") {
            "${System.getProperty("java.runtime.name")} ${System.getProperty("java.runtime.version")}"
        }

        val JavaVendor = JvmInfo("Java Vendor") {
            System.getProperty("java.vendor")
        }

        val WorkingDirectory = JvmInfo("Working Directory") {
            System.getProperty("user.dir")
        }

        val JavaVersion = JvmInfo("Java Version") {
            System.getProperty("java.version")
        }

        val UserName = JvmInfo("User Name") {
            System.getenv("username")
        }

        val HostName = JvmInfo("Host Name") {
            InetAddress.getLocalHost().hostName
        }

        fun all() = listOf(
            JavaHome,
            JavaJRE,
            JavaVendor,
            JavaVersion,
            HostName,
            UserName,
            WorkingDirectory
        )
    }
}