package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface IJvmImpl {

    val defaultStatusBarForegroundColor: Color
        @Composable get

    val defaultStatusBarBackgroundColor: Color
        @Composable get
}

object JvmImpl {
    lateinit var instance: IJvmImpl
        private set

    fun init(instance: IJvmImpl) {
        this.instance = instance
    }
}