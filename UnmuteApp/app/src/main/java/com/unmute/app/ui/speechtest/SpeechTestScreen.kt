package com.unmute.app.ui.speechtest

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.unmute.app.speech.SpeechCaptioner

@Composable
fun SpeechTestScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    val available = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    var listening by remember { mutableStateOf(false) }
    var partial by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Siap") }
    val sentences = remember { mutableStateListOf<String>() }

    val captioner = remember {
        SpeechCaptioner(
            context = context,
            onPartial = { partial = it },
            onSentence = {
                sentences.add(it)
                partial = ""
            },
            onStatus = { status = it },
        )
    }
    DisposableEffect(Unit) { onDispose { captioner.release() } }

    val view = LocalView.current
    DisposableEffect(listening) {
        view.keepScreenOn = listening
        onDispose { view.keepScreenOn = false }
    }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack, enabled = !listening) { Text("Kembali") }
        Text("Tes Pengenal Suara", style = MaterialTheme.typography.headlineSmall)
        Text(
            if (available) "Status: $status" else "HP ini tidak punya layanan pengenal suara",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                when {
                    !hasPermission -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    listening -> { captioner.stop(); listening = false }
                    else -> { captioner.start(); listening = true }
                }
            },
            enabled = available,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                when {
                    !hasPermission -> "Izinkan Mikrofon"
                    listening -> "Berhenti"
                    else -> "Mulai Dengarkan"
                }
            )
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        CaptionParagraph(
            sentences = sentences,
            partial = partial,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CaptionParagraph(
    sentences: List<String>,
    partial: String,
    modifier: Modifier = Modifier,
) {
    val fadedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    val finalText = sentences.joinToString(" ") { it.trim() }
    val paragraph = buildAnnotatedString {
        append(finalText)
        if (partial.isNotBlank()) {
            if (finalText.isNotEmpty()) append(" ")
            withStyle(SpanStyle(color = fadedColor)) { append(partial.trim()) }
        }
    }

    val scrollState = rememberScrollState()
    LaunchedEffect(paragraph.text) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Text(
        text = paragraph,
        fontSize = 20.sp,
        lineHeight = 30.sp,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    )
}
