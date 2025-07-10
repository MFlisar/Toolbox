package com.michaelflisar.toolbox.app.features.device

expect val CurrentDevice: Device

enum class BaseDevice {
    Desktop,
    Mobile,
    Web
}

enum class Device(
    val base: BaseDevice
) {
    Android(BaseDevice.Mobile),
    iOS(BaseDevice.Mobile),
    Windows(BaseDevice.Desktop),
    MacOS(BaseDevice.Desktop),
    Linux(BaseDevice.Desktop),
    WASM(BaseDevice.Web)
}