package com.unmute.app.ui.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmute.app.data.model.CreateClassroomResponse
import com.unmute.app.data.repository.ClassroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateClassroomUiState(
    val name: String = "",
    val courseCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val created: CreateClassroomResponse? = null,
)

class CreateClassroomViewModel(
    private val repository: ClassroomRepository = ClassroomRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateClassroomUiState())
    val uiState: StateFlow<CreateClassroomUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }

    fun onCourseCodeChange(value: String) = _uiState.update { it.copy(courseCode = value, error = null) }

    fun create() {
        val state = _uiState.value
        val name = state.name.trim()
        val courseCode = state.courseCode.trim()
        val error = when {
            name.isEmpty() -> "Nama kelas wajib diisi"
            courseCode.isEmpty() -> "Kode mata kuliah wajib diisi"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.createClassroom(name, courseCode)
                .onSuccess { res -> _uiState.update { it.copy(isLoading = false, created = res) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun resetForNew() {
        _uiState.value = CreateClassroomUiState()
    }
}
