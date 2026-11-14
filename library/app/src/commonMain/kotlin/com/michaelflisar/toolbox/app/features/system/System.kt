package com.michaelflisar.toolbox.app.features.system

object System

expect val System.kill: (() -> Unit)?
expect val System.restart: (() -> Unit)?
