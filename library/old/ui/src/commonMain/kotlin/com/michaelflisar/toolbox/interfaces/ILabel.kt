package com.michaelflisar.toolbox.interfaces

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

interface ILabel {

    @Composable
    fun label(): String
}

interface ILabelResource : ILabel {
    val label: StringResource

    @Composable
    override fun label() = stringResource(label)
}