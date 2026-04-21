package com.michaelflisar.toolbox.ads

import com.michaelflisar.toolbox.features.ads.BaseAdManager

actual val AdManager.DEFAULT_IMPL: BaseAdManager?
    get() = null