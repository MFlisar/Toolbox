package com.michaelflisar.toolbox.app.features.device

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.ui.graphics.vector.ImageVector

expect val CurrentDevice: Device

enum class BaseDevice {
    Desktop,
    Mobile,
    Web
}

enum class Device(
    val base: BaseDevice,
    val icon: ImageVector
) {
    Android(BaseDevice.Mobile, Icons.Default.PhoneAndroid),
    iOS(BaseDevice.Mobile, Icons.Default.PhoneIphone),
    Windows(BaseDevice.Desktop, Icons.Default.Computer),
    MacOS(BaseDevice.Desktop, Icons.Default.Computer),
    Linux(BaseDevice.Desktop, Icons.Default.Computer),
    WASM(BaseDevice.Web, Icons.Default.Computer)
}