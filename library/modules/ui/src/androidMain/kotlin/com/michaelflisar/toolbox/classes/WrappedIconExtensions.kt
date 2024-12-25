package com.michaelflisar.toolbox.classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun Int.asWrappedIcon() = WrappedIcon.Painter(painterResource(this))