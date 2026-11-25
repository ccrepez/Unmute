package com.unmute.app.ui.caption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmute.app.data.model.Caption
import com.unmute.app.data.repository.CaptionRepository
import com.unmute.app.data.repository.QuestionRepository
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
    val questionDraft: String = "",
    val isSendingQuestion: Boolean = false,
    val questionMessage: String? = null,
) {
    val canSendQuestion: Boolean
        get() = !isEnded && !isSendingQuestion && questionDraft.isNotBlank() && questionDraft.length <= MAX_QUESTION_LENGTH

    val canDecreaseFont: Boolean get() = fontSizeSp > MIN_FONT_SP
    val canIncreaseFont: Boolean get() = fontSizeSp < MAX_FONT_SP

    fun withLargerFont() = copy(fontSizeSp = (fontSizeSp + FONT_STEP_SP).coerceAtMost(MAX_FONT_SP))

    fun withSmallerFont() = copy(fontSizeSp = (fontSizeSp - FONT_STEP_SP).coerceAtLeast(MIN_FONT_SP))

    companion object {
        const val DEFAULT_FONT_SP = 18
        const val MIN_FONT_SP = 14
        const val MAX_FONT_SP = 36
        const val FONT_STEP_SP = 2
        const val MAX_QUESTION_LENGTH = 300
    }
}

class CaptionLiveViewModel(
    private val sessionId: String,
    private val repository: CaptionRepository = CaptionRepository(),
    private val questionRepository: QuestionRepository = QuestionRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptionLiveUiState())
    val uiState: StateFlow<CaptionLiveUiState> = _uiState.asStateFlow()

    init {
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

    fun increaseFont() = _uiState.update { it.withLargerFont() }

    fun decreaseFont() = _uiState.update { it.withSmallerFont() }

    fun onQuestionChange(text: String) = _uiState.update {
        it.copy(questionDraft = text.take(CaptionLiveUiState.MAX_QUESTION_LENGTH))
    }

    fun submitQuestion() {
        val state = _uiState.value
        if (!state.canSendQuestion) return
        val text = state.questionDraft.trim()
        _uiState.update { it.copy(isSendingQuestion = true, questionDraft = "") }
        viewModelScope.launch {
            runCatching {
                questionRepository.submitQuestion(
                    sessionId = sessionId,
                    studentId = QuestionRepository.TEST_STUDENT_ID,
                    studentName = QuestionRepository.TEST_STUDENT_NAME,
                    text = text,
                )
            }.onSuccess {
                _uiState.update { it.copy(isSendingQuestion = false, questionMessage = "Pertanyaan terkirim ke dosen") }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isSendingQuestion = false,
                        questionDraft = text,
                        questionMessage = "Gagal mengirim pertanyaan: ${e.message}",
                    )
                }
            }
        }
    }

    fun onQuestionMessageShown() = _uiState.update { it.copy(questionMessage = null) }
}
