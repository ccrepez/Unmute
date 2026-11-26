package com.unmute.app.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.unmute.app.ui.auth.AccountStatusCard
import com.unmute.app.ui.auth.LoginScreen
import com.unmute.app.ui.auth.RegisterScreen
import com.unmute.app.ui.caption.CaptionLiveScreen
import com.unmute.app.ui.notes.LectureNotesScreen
import com.unmute.app.ui.speechtest.SpeechTestScreen
import kotlinx.serialization.Serializable

@Serializable
data object DevMenuRoute

@Serializable
data class CaptionLiveRoute(val sessionId: String)

@Serializable
data class LectureNotesRoute(val sessionId: String)

@Serializable
data object SpeechTestRoute

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

@Composable
fun UnmuteNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = DevMenuRoute) {
        composable<DevMenuRoute> {
            DevMenuScreen(
                onSpeechTest = { navController.navigate(SpeechTestRoute) },
                onStudent = { navController.navigate(CaptionLiveRoute("test")) },
                onLogin = { navController.navigate(LoginRoute) },
            )
        }
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
        composable<SpeechTestRoute> {
            SpeechTestScreen(onBack = { navController.popBackStack() })
        }
        composable<LoginRoute> {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoggedIn = { navController.popBackStack<DevMenuRoute>(inclusive = false) },
                onOpenRegister = { navController.navigate(RegisterRoute) },
            )
        }
        composable<RegisterRoute> {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegistered = { navController.popBackStack<DevMenuRoute>(inclusive = false) },
            )
        }
    }
}

@Composable
private fun DevMenuScreen(
    onSpeechTest: () -> Unit,
    onStudent: () -> Unit,
    onLogin: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Unmute (mode tes)", style = MaterialTheme.typography.headlineSmall)
        AccountStatusCard(onLogin = onLogin)
        Button(onClick = onSpeechTest, modifier = Modifier.fillMaxWidth()) { Text("Tes Pengenal Suara") }
        OutlinedButton(onClick = onStudent, modifier = Modifier.fillMaxWidth()) { Text("Masuk sebagai Mahasiswa") }
    }
}
