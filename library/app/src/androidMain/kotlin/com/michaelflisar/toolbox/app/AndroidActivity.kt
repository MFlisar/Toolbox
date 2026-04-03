package com.michaelflisar.toolbox.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

abstract class AndroidActivity : ComponentActivity() {

    private val lastIntent = mutableStateOf(Intent())

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastIntent.value = intent

        // Init function
        AndroidApp.initActivity(this)

        setContent {
            Content()
        }
    }

    @Composable
    abstract fun Content()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lastIntent.value = intent
    }
}