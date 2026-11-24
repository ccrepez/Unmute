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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unmute.app.data.model.Caption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptionLiveScreen(
    sessionId: String,
    onBack: () -> Unit,
    onOpenNotes: () -> Unit = {},
    viewModel: CaptionLiveViewModel = viewModel(key = sessionId) { CaptionLiveViewModel(sessionId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Gulir otomatis ke caption terbaru setiap kali jumlahnya bertambah.
    LaunchedEffect(state.captions.size) {
        if (state.captions.isNotEmpty()) listState.animateScrollToItem(state.captions.lastIndex)
    }

    Scaffold(
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
                    OutlinedButton(onClick = viewModel::decreaseFont, contentPadding = PaddingValues(8.dp)) { Text("A−") }
                    Spacer(Modifier.width(4.dp))
                    OutlinedButton(onClick = viewModel::increaseFont, contentPadding = PaddingValues(8.dp)) { Text("A+") }
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
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.captions, key = { it.id }) { caption ->
                            CaptionItem(caption, state.fontSizeSp)
                        }
                    }
                }
            }

            if (state.isEnded) {
                SessionEndedBar(onOpenNotes)
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
            .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun CaptionItem(caption: Caption, fontSizeSp: Int) {
    Card(Modifier.fillMaxWidth()) {
        Text(
            caption.text,
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp * 1.4).sp,
            modifier = Modifier.padding(12.dp),
        )
    }
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
