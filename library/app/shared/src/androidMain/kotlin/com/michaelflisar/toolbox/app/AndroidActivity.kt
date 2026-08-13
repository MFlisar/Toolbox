package com.michaelflisar.toolbox.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

abstract class AndroidActivity : ComponentActivity() {

    val lastIntent = mutableStateOf<Intent?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        lastIntent.value = intent

        // Init function
        AndroidApp.initActivity(this)

        setContent {
            Content()
        }

        onIntent(intent)
    }

    @Composable
    abstract fun Content()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lastIntent.value = intent
        onIntent(intent)
    }

    open fun onIntent(intent: Intent) {}
}