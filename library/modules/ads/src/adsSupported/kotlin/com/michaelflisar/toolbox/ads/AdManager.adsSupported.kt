package com.michaelflisar.toolbox.ads

import app.lexilabs.basic.ads.AdUnitId
import com.michaelflisar.toolbox.features.ads.BaseAdManager

actual val AdManager.DEFAULT_IMPL: BaseAdManager?
    get() = AdManagerImpl

val AdManager.BANNER_DEFAULT : String
    get() = AdUnitId.BANNER_DEFAULT