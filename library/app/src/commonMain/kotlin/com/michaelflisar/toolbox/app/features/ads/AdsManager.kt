package com.michaelflisar.toolbox.app.features.ads

import com.michaelflisar.toolbox.features.ads.BaseAdManager

object AdsManager {

    private var _manager: BaseAdManager? = null
    val manager: BaseAdManager?
        get() = _manager

    fun init(manager: BaseAdManager) {
        _manager = manager
    }
}