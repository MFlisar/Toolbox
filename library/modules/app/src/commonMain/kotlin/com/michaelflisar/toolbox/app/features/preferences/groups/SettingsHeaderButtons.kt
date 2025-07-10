package com.michaelflisar.toolbox.app.features.preferences.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composepreferences.core.PreferenceCustom
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.toolbox.classes.WrappedIcon
import com.michaelflisar.toolbox.components.MyWrappedIcon

object SettingsHeaderButtons {

    class Button internal constructor(
        val text: String,
        val icon: WrappedIcon,
        val onClick: () -> Unit,
        val color: ButtonColors,
    )

    @Composable
    fun button(
        text: String,
        icon: WrappedIcon,
        color: ButtonColors = ButtonDefaults.buttonColors(),
        onClick: () -> Unit,
    ) = Button(text, icon, onClick, color)

    @Composable
    fun buttonPrimary(
        text: String,
        icon: WrappedIcon,
        onClick: () -> Unit,
    ) = Button(
        text, icon, onClick,
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )

    @Composable
    fun buttonSecondary(
        text: String,
        icon: WrappedIcon,
        onClick: () -> Unit,
    ) = Button(
        text, icon, onClick,
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
fun PreferenceGroupScope.SettingsHeaderButtons(
    buttons: List<SettingsHeaderButtons.Button>,
    chunks: Int = 2,
) {
    if (buttons.isNotEmpty()) {

        PreferenceSection(
            title = ""
        ) {
            PreferenceCustom {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    buttons.chunked(chunks).forEach { chunk ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chunk.forEach {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = it.onClick,
                                    colors = it.color
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        MyWrappedIcon(
                                            it.icon,
                                            Modifier
                                        )
                                        Text(it.text)
                                    }
                                }
                            }
                            if (chunk.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}