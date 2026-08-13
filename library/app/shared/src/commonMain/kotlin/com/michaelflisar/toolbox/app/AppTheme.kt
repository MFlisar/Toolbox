package com.michaelflisar.toolbox.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppTheme.Companion.getOnToolbarColorFromPrefs
import com.michaelflisar.toolbox.app.AppTheme.Companion.getToolbarColorFromPrefs
import com.michaelflisar.toolbox.app.features.theme.ThemeSetup
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.app.platform.UpdateComposeThemeStatusBar

@Stable
class AppTheme(
    val statusBarColor: Color,
    val navigationBarColor: Color,
    val isDark: Boolean,
    val toolbarColor: Color,
    val onToolbarColor: Color,
    val updateStatusBar: @Composable (() -> Unit),
) {
    companion object {

        @Composable
        fun getToolbarColorFromPrefs(): Color {
            return when (AppSetup.get().prefs.toolbarStyle.collectAsStateNotNull().value) {
                ToolbarStyle.Primary -> MaterialTheme.colorScheme.primary
                ToolbarStyle.Background -> MaterialTheme.colorScheme.background
                ToolbarStyle.Black -> Color.Black
            }
        }

        @Composable
        fun getOnToolbarColorFromPrefs(): Color {
            return when (AppSetup.get().prefs.toolbarStyle.collectAsStateNotNull().value) {
                ToolbarStyle.Primary -> MaterialTheme.colorScheme.onPrimary
                ToolbarStyle.Background -> MaterialTheme.colorScheme.onBackground
                ToolbarStyle.Black -> Color.White
            }
        }
    }
}

@Composable
fun rememberAppTheme(
    statusBarColor: Color = getToolbarColorFromPrefs(),
    navigationBarColor: Color = NavigationBarDefaults.containerColor,
    isDark: Boolean = LocalComposeTheme.current.base.value.isDark(),
    toolbarColor: Color = getToolbarColorFromPrefs(),
    onToolbarColor: Color = getOnToolbarColorFromPrefs(),
): AppTheme {
    return remember(
        statusBarColor,
        navigationBarColor,
        isDark,
        toolbarColor,
        onToolbarColor,
    ) {
        AppTheme(
            statusBarColor = statusBarColor,
            navigationBarColor = navigationBarColor,
            isDark = isDark,
            toolbarColor = toolbarColor,
            onToolbarColor = onToolbarColor,
            updateStatusBar = {
                Platform.UpdateComposeThemeStatusBar(
                    statusBarColor,
                    navigationBarColor,
                    isDark
                )
            }
        )
    }
}

@Composable
fun rememberComposeTheme() = ThemeSetup.get().rememberComposeThemeDefault()

@Composable
fun AppTheme(
    composeTheme: ComposeTheme.State,
    appThemeProvider: @Composable () -> AppTheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalComposeTheme provides composeTheme
    ) {
        val appTheme = appThemeProvider()
        CompositionLocalProvider(
            LocalAppTheme provides appTheme
        ) {
            appTheme.updateStatusBar()
            content()
        }
    }
}

internal val LocalComposeTheme = staticCompositionLocalOf<ComposeTheme.State> { throw IllegalStateException("No LocalComposeTheme provided") }
internal val LocalAppTheme = staticCompositionLocalOf<AppTheme> { throw IllegalStateException("No LocalAppTheme provided") }