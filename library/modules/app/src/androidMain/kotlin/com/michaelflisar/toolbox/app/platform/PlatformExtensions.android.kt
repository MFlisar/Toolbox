package com.michaelflisar.toolbox.app.platform

import android.content.Context
import com.michaelflisar.toolbox.app.classes.PlatformContext

val PlatformContext.androidContext: Context
    get() = this.context as Context