package com.unmute.app.ui.caption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unmute.app.data.model.Caption
import com.unmute.app.ui.theme.LiveRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptionLiveScreen(
    sessionId: String,
    onBack: () -> Unit,
    onOpenNotes: () -> Unit = {},
    viewModel: CaptionLiveViewModel = viewModel(key = sessionId) { CaptionLiveViewModel(sessionId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.questionMessage) {
        state.questionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onQuestionMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(state.title.ifBlank { "Sesi $sessionId" }, maxLines = 1)
                        Spacer(Modifier.width(8.dp))
                        if (!state.isEnded) LiveBadge()
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = viewModel::decreaseFont,
                        enabled = state.canDecreaseFont,
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.semantics { contentDescription = "Perkecil huruf" },
                    ) { Text("A−") }
                    Spacer(Modifier.width(4.dp))
                    OutlinedButton(
                        onClick = viewModel::increaseFont,
                        enabled = state.canIncreaseFont,
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.semantics { contentDescription = "Perbesar huruf" },
                    ) { Text("A+") }
                    Spacer(Modifier.width(8.dp))
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            state.error?.let {
                Text(
                    "Gagal memuat caption: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                )
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                if (state.captions.isEmpty()) {
                    Text(
                        "Menunggu dosen mulai berbicara…",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    CaptionParagraph(state.captions, state.fontSizeSp)
                }
            }

            if (state.isEnded) {
                SessionEndedBar(onOpenNotes)
            } else {
                QuestionInputBar(
                    draft = state.questionDraft,
                    canSend = state.canSendQuestion,
                    isSending = state.isSendingQuestion,
                    onDraftChange = viewModel::onQuestionChange,
                    onSend = viewModel::submitQuestion,
                )
            }
        }
    }
}

@Composable
private fun CaptionParagraph(captions: List<Caption>, fontSizeSp: Int) {
    val scrollState = rememberScrollState()
    val paragraph = remember(captions) { captions.joinToString(" ") { it.text.trim() } }

    LaunchedEffect(paragraph, fontSizeSp) {
        if (scrollState.value <= FOLLOW_THRESHOLD_PX) scrollState.animateScrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState, reverseScrolling = true)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Text(
            paragraph,
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp * 1.5).sp,
        )
    }
}

private const val FOLLOW_THRESHOLD_PX = 48

@Composable
private fun QuestionInputBar(
    draft: String,
    canSend: Boolean,
    isSending: Boolean,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis pertanyaan untuk dosen…") },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send,
                ),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
            )
            Spacer(Modifier.width(8.dp))
            if (isSending) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onSend, enabled = canSend) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim pertanyaan")
                }
            }
        }
    }
}

@Composable
private fun LiveBadge() {
    Text(
        "LIVE",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier
            .background(LiveRed, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun SessionEndedBar(onOpenNotes: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Sesi sudah berakhir", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.padding(4.dp))
        Button(onClick = onOpenNotes) { Text("Lihat Catatan Kuliah") }
    }
}
