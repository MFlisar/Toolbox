package com.michaelflisar.toolbox.androiddemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.UpdateEdgeToEdgeDefault
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.androiddemoapp.classes.DemoPrefs

data class DemoData(val snackbarHostState: SnackbarHostState) {
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        dismissPrevious: Boolean = false
    ): SnackbarResult {
        if (dismissPrevious) {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        return snackbarHostState.showSnackbar(message, actionLabel, withDismissAction, duration)
    }

}

val LocalDemo = staticCompositionLocalOf { DemoData(SnackbarHostState()) }

abstract class DemoActivity(
    private val scrollableContent: Boolean = true,
    private val contentPadding: Dp = 16.dp,
    private val contentVerticalSpacing: Dp = 8.dp
) : ComponentActivity() {

    @Composable
    abstract fun ColumnScope.Content(
        themeState: ComposeTheme.State
    )

    @Composable
    open fun createBottomBar(): (@Composable () -> Unit)? {
        return null
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val baseTheme = DemoPrefs.baseTheme.collectAsStateNotNull()
            val contrast = DemoPrefs.contrast.collectAsStateNotNull()
            val dynamic = DemoPrefs.dynamic.collectAsStateNotNull()
            val theme = DemoPrefs.theme.collectAsStateNotNull()
            val state = ComposeTheme.State(baseTheme, contrast, dynamic, theme)

            ComposeTheme(state = state) {

                UpdateEdgeToEdgeDefault(this, state)

                val bottomBar = createBottomBar()
                val demoData = DemoData(SnackbarHostState())
                val snackbarHostState = remember { demoData.snackbarHostState }

                CompositionLocalProvider(LocalDemo provides demoData) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = stringResource(R.string.app_name))
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        },
                        bottomBar = { bottomBar?.invoke() },
                        content = { padding ->
                            Content(
                                modifier = Modifier
                                    .fillMaxSize()
                                    // consume insets as scaffold doesn't do it by default
                                    .padding(padding)
                                    .consumeWindowInsets(padding)
                                    .then(if (bottomBar == null) Modifier.navigationBarsPadding() else Modifier)
                                    .then(
                                        if (scrollableContent) Modifier.verticalScroll(
                                            rememberScrollState()
                                        ) else Modifier
                                    )
                                    .padding(contentPadding),
                                state = state
                            )
                        }
                    )
                }
            }
        }
    }

    // ----------------
    // UI - Content
    // ----------------

    @Composable
    private fun Content(
        modifier: Modifier,
        state: ComposeTheme.State
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(contentVerticalSpacing)
        ) {
            Content(state)
        }
    }
}