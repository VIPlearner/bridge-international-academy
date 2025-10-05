package com.bridge.androidtechnicaltest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.bridge.androidtechnicaltest.ui.navigation.AppNavGraph
import com.bridge.androidtechnicaltest.ui.theme.TechnicalTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechnicalTestTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}