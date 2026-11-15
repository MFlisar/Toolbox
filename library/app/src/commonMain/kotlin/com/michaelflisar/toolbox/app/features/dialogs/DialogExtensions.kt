package com.michaelflisar.toolbox.app.features.dialogs

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.layout.StyledDialogDefaults
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.button_cancel
import com.michaelflisar.toolbox.core.resources.button_no
import com.michaelflisar.toolbox.core.resources.button_ok
import com.michaelflisar.toolbox.core.resources.button_yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun StyledDialogDefaults.buttonOK(
    onClick: () -> Unit,
) = StyledDialog.Buttons(
    positive = StyledDialog.Button(
        onClick = onClick,
        label = stringResource(Res.string.button_ok)
    )
)

@Composable
fun StyledDialogDefaults.buttonCancel(
    onClick: () -> Unit,
) = StyledDialog.Buttons(
    positive = StyledDialog.Button(
        onClick = onClick,
        label = stringResource(Res.string.button_cancel)
    )
)

@Composable
fun StyledDialogDefaults.buttonsYesNo(
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
) = StyledDialog.Buttons(
    positive = StyledDialog.Button(
        onClick = onYesClick,
        label = stringResource(Res.string.button_yes)
    ),
    negative = StyledDialog.Button(
        onClick = onNoClick,
        label = stringResource(Res.string.button_no)
    )
)