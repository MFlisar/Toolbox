package com.michaelflisar.toolbox.app.features.activity

import androidx.compose.runtime.compositionLocalOf

expect class Activity

val LocalActivity = compositionLocalOf<Activity> { throw RuntimeException("Activity not initialised!") }