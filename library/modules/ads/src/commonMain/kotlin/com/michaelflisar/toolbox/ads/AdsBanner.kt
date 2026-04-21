package com.michaelflisar.toolbox.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.placeholder_ads
import com.michaelflisar.toolbox.features.ads.BaseAdManager
import org.jetbrains.compose.resources.StringResource

@Composable
fun AdsBanner(
    adManager: BaseAdManager?,
    adUnitId: String,
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    placeholderResource: StringResource = Res.string.placeholder_ads,
) {
    adManager?.AdsBanner(
        adUnitId = adUnitId,
        modifier = modifier,
        visible = visible,
        placeholderResource = placeholderResource
    )
}