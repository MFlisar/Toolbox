package com.michaelflisar.toolbox.features.proversion

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource
import com.michaelflisar.composedialogs.core.DialogStateNoData

interface BaseAppProVersionManager {
    val proState: StateFlow<ProState>
    suspend fun checkProVersion(): ProState

    @Composable
    fun ProVersionDialog(
        dialogState: DialogStateNoData,
        infoTextResource: StringResource
    )
}