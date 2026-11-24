package com.unmute.app.ui.caption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmute.app.data.model.Caption
import com.unmute.app.data.repository.CaptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CaptionLiveUiState(
    val title: String = "",
    val captions: List<Caption> = emptyList(),
    val isEnded: Boolean = false,
    val fontSizeSp: Int = DEFAULT_FONT_SP,
    val error: String? = null,
) {
    companion object {
        const val DEFAULT_FONT_SP = 18
        const val MIN_FONT_SP = 14
        const val MAX_FONT_SP = 36
        const val FONT_STEP_SP = 2
    }
}

class CaptionLiveViewModel(
    private val sessionId: String,
    private val repository: CaptionRepository = CaptionRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptionLiveUiState())
    val uiState: StateFlow<CaptionLiveUiState> = _uiState.asStateFlow()

    init {
        // Kedua listener hidup di viewModelScope, jadi otomatis dilepas
        // saat mahasiswa keluar dari layar (ViewModel di-clear).
        viewModelScope.launch {
            repository.observeCaptions(sessionId)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { captions -> _uiState.update { it.copy(captions = captions, error = null) } }
        }
        viewModelScope.launch {
            repository.observeSession(sessionId)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { session ->
                    _uiState.update {
                        it.copy(
                            title = session?.title.orEmpty(),
                            isEnded = session?.status == "ENDED",
                        )
                    }
                }
        }
    }

    fun increaseFont() = _uiState.update {
        it.copy(fontSizeSp = (it.fontSizeSp + CaptionLiveUiState.FONT_STEP_SP).coerceAtMost(CaptionLiveUiState.MAX_FONT_SP))
    }

    fun decreaseFont() = _uiState.update {
        it.copy(fontSizeSp = (it.fontSizeSp - CaptionLiveUiState.FONT_STEP_SP).coerceAtLeast(CaptionLiveUiState.MIN_FONT_SP))
    }
}
