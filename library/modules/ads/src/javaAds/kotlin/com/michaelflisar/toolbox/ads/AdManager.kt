package com.michaelflisar.toolbox.ads

import android.Manifest
import android.app.Activity
import androidx.annotation.RequiresPermission
import app.lexilabs.basic.ads.AdUnitId
import app.lexilabs.basic.ads.BasicAds

object AdManager {

    object Ids {
        const val BANNER_DEFAULT = AdUnitId.BANNER_DEFAULT
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    fun init(activity: Activity) {
        // initialize ads
        BasicAds.initialize(activity)
    }
}