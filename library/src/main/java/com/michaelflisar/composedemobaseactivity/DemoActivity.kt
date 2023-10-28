package com.michaelflisar.composedemobaseactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelflisar.composedemobaseactivity.classes.AppPrefs
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import com.michaelflisar.composedemobaseactivity.theme.AppTheme
import com.michaelflisar.kotpreferences.compose.collectAsState

abstract class DemoActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val stateTheme = AppPrefs.theme.collectAsState()
            val stateDynamicTheme = AppPrefs.dynamicTheme.collectAsState()
            val theme = stateTheme.value
            val dynamicTheme = stateDynamicTheme.value

            if (theme == null || dynamicTheme == null)
                return@setContent

            onInit()

            AppTheme(
                darkTheme = theme.isDark(),
                dynamicColor = dynamicTheme
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Content(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            theme,
                            dynamicTheme
                        )
                    }
                }
            }
        }
    }

    // ----------------
    // UI - Content
    // ----------------

    @Composable
    open fun onInit() {}

    @Composable
    abstract fun Content(
        modifier: Modifier,
        theme: DemoTheme,
        dynamicTheme: Boolean
    )
}