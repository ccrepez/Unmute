package com.unmute.app.ui.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmute.app.data.model.JoinClassroomResponse
import com.unmute.app.data.repository.ClassroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JoinClassroomUiState(
    val joinCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val joined: JoinClassroomResponse? = null,
)

class JoinClassroomViewModel(
    private val repository: ClassroomRepository = ClassroomRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinClassroomUiState())
    val uiState: StateFlow<JoinClassroomUiState> = _uiState.asStateFlow()

    fun onJoinCodeChange(value: String) {
        val cleaned = value.uppercase().filter { !it.isWhitespace() }.take(8)
        _uiState.update { it.copy(joinCode = cleaned, error = null) }
    }

    fun join() {
        val code = _uiState.value.joinCode.trim().uppercase()
        if (code.isEmpty()) {
            _uiState.update { it.copy(error = "Kode kelas wajib diisi") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.joinClassroom(code)
                .onSuccess { res -> _uiState.update { it.copy(isLoading = false, joined = res) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
