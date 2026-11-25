package com.unmute.app.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmute.app.data.model.SessionNotes
import com.unmute.app.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LectureNotesUiState(
    val isLoading: Boolean = true,
    val notes: SessionNotes? = null,
    val error: String? = null,
)

class LectureNotesViewModel(
    private val sessionId: String,
    private val repository: NotesRepository = NotesRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(LectureNotesUiState())
    val uiState: StateFlow<LectureNotesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getSessionNotes(sessionId)
                .onSuccess { notes -> _uiState.update { it.copy(isLoading = false, notes = notes) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
