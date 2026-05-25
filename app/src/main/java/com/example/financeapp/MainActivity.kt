package com.example.financeapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.financeapp.ui.navigation.AppNavigation
import com.example.financeapp.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FinanceAppTheme {
                val view = LocalView.current
                val backgroundColor = MaterialTheme.colorScheme.background
                val isDarkTheme = isSystemInDarkTheme()

                SideEffect {
                    val window = (view.context as Activity).window

                    window.statusBarColor = backgroundColor.toArgb()
                    window.navigationBarColor = backgroundColor.toArgb()

                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                        !isDarkTheme

                    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                        !isDarkTheme
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}