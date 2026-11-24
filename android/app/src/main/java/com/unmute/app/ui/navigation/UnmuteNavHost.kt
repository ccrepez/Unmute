package com.unmute.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.unmute.app.ui.caption.CaptionLiveScreen
import kotlinx.serialization.Serializable

/** Layar Caption Live. Minggu 1 memakai sesi "test"; nanti dibuka dari Halaman Kelas (Ina). */
@Serializable
data class CaptionLiveRoute(val sessionId: String)

@Composable
fun UnmuteNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = CaptionLiveRoute("test")) {
        composable<CaptionLiveRoute> { entry ->
            val route = entry.toRoute<CaptionLiveRoute>()
            CaptionLiveScreen(
                sessionId = route.sessionId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
