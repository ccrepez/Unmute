package com.unmute.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.unmute.app.ui.navigation.UnmuteNavHost
import com.unmute.app.ui.theme.UnmuteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnmuteTheme {
                UnmuteNavHost()
            }
        }
    }
}
