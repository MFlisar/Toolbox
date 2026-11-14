package com.michaelflisar.toolbox.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

abstract class AndroidActivity : ComponentActivity() {

    val lastIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        lastIntent.value = intent

        App.initActivity(this)

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
