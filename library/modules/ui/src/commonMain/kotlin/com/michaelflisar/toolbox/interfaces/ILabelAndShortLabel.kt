package com.michaelflisar.toolbox.interfaces

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

interface ILabelAndShortLabel : ILabel {
    @Composable
    fun labelShort(): String
}

interface ILabelAndShortLabelResource : ILabelAndShortLabel, ILabelResource {
    val labelShort: StringResource

    @Composable
    override fun labelShort() = stringResource(labelShort)
}