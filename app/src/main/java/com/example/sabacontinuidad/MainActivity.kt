package com.example.sabacontinuidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sabacontinuidad.navigation.AppNavigation
import com.example.sabacontinuidad.ui.theme.SabaContinuidadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SabaContinuidadTheme {
                AppNavigation()
            }
        }
    }
}