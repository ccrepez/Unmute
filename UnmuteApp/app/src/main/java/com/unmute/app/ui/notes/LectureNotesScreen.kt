package com.unmute.app.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureNotesScreen(
    sessionId: String,
    onBack: () -> Unit,
    viewModel: LectureNotesViewModel = viewModel(key = "notes-$sessionId") { LectureNotesViewModel(sessionId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                title = { Text(state.notes?.title ?: "Catatan Kuliah", maxLines = 1) },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> Column(
                    Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Gagal memuat catatan: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load, modifier = Modifier.padding(top = 8.dp)) { Text("Coba lagi") }
                }

                state.notes != null -> {
                    val notes = state.notes!!
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            Text("Transkrip (${notes.captionCount} kalimat)", style = MaterialTheme.typography.titleMedium)
                        }
                        item {
                            Card(Modifier.fillMaxWidth()) {
                                Text(notes.transcript, modifier = Modifier.padding(12.dp))
                            }
                        }
                        item {
                            Text("Pertanyaan", style = MaterialTheme.typography.titleMedium)
                        }
                        if (notes.questions.isEmpty()) {
                            item { Text("Tidak ada pertanyaan di sesi ini.") }
                        }
                        items(notes.questions) { q ->
                            Text("${if (q.isAnswered) "✓" else "•"} ${q.text}")
                        }
                    }
                }
            }
        }
    }
}
