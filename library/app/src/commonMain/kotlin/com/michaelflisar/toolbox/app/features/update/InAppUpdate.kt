package com.michaelflisar.toolbox.app.features.update

enum class InAppUpdate {
    Disabled,
    /** Starts the dismissible Google Play flexible update flow. */
    Dialog,
    /** Starts the blocking Google Play immediate update flow. */
    Force,
}
