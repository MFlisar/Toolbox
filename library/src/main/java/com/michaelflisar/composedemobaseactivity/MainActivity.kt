package com.michaelflisar.composedemobaseactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedemobaseactivity.classes.DemoPrefs
import com.michaelflisar.composedemobaseactivity.composables.DemoAppThemeRegionDetailed
import com.michaelflisar.composedemobaseactivity.composables.rememberExpandedRegions
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.UpdateEdgeToEdgeDefault
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull

abstract class MainActivity(
    private val scrollableContent: Boolean = true
) : ComponentActivity() {

    @Composable
    abstract fun ColumnScope.Content()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val baseTheme = DemoPrefs.baseTheme.collectAsStateNotNull()
            val dynamic = DemoPrefs.dynamic.collectAsStateNotNull()
            val theme = DemoPrefs.themeKey.collectAsStateNotNull()
            val state = ComposeTheme.State(baseTheme, dynamic, theme)

            ComposeTheme(state = state) {

                UpdateEdgeToEdgeDefault(this, state)

                Scaffold(
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
                    content = { padding ->
                        Content(
                            modifier = Modifier
                                .fillMaxSize()
                                // consume insets as scaffold doesn't do it by default
                                .consumeWindowInsets(padding)
                                .then(
                                    if (scrollableContent) Modifier.verticalScroll(
                                        rememberScrollState()
                                    ) else Modifier
                                )
                                .padding(padding)
                                .padding(16.dp)
                        )
                    }
                )
            }
        }
    }

    // ----------------
    // UI - Content
    // ----------------

    @Composable
    private fun Content(
        modifier: Modifier
    ) {
        val regionsState = rememberExpandedRegions(listOf(1))
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DemoAppThemeRegionDetailed(
                state = regionsState
            )
            Content()
        }
    }
}