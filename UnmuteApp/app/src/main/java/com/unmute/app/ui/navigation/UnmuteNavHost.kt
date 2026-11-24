package com.unmute.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.unmute.app.ui.caption.CaptionLiveScreen
import com.unmute.app.ui.notes.LectureNotesScreen
import kotlinx.serialization.Serializable

/** Layar Caption Live. Minggu 1 memakai sesi "test"; nanti dibuka dari Halaman Kelas (Ina). */
@Serializable
data class CaptionLiveRoute(val sessionId: String)

/** Layar Catatan Kuliah untuk sesi yang sudah ENDED. */
@Serializable
data class LectureNotesRoute(val sessionId: String)

@Composable
fun UnmuteNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = CaptionLiveRoute("test")) {
        composable<CaptionLiveRoute> { entry ->
            val route = entry.toRoute<CaptionLiveRoute>()
            CaptionLiveScreen(
                sessionId = route.sessionId,
                onBack = { navController.popBackStack() },
                onOpenNotes = {
                    navController.navigate(LectureNotesRoute(route.sessionId)) {
                        popUpTo<CaptionLiveRoute> { inclusive = true }
                    }
                },
            )
        }
        composable<LectureNotesRoute> { entry ->
            val route = entry.toRoute<LectureNotesRoute>()
            LectureNotesScreen(
                sessionId = route.sessionId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
