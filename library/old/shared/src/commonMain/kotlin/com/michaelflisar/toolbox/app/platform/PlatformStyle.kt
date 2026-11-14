package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.Current
import com.michaelflisar.toolbox.app.features.device.Device

enum class PlatformStylePreference {
    Auto,
    Mobile,
    Desktop,
    Web
    ;

    internal fun resolve(): ResolvedPlatformStyle {
        return when (this) {
            Auto -> when (Device.Current.base) {
                BaseDevice.Mobile -> ResolvedPlatformStyle.Mobile
                BaseDevice.Desktop -> ResolvedPlatformStyle.Desktop
                BaseDevice.Web -> ResolvedPlatformStyle.Web
            }

            Mobile -> ResolvedPlatformStyle.Mobile
            Desktop -> ResolvedPlatformStyle.Desktop
            Web -> ResolvedPlatformStyle.Web
        }
    }
}

enum class ResolvedPlatformStyle {
    Mobile,
    Desktop,
    Web
}