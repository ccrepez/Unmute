package com.unmute.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmute.app.data.model.UserProfile
import com.unmute.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: String = UserProfile.ROLE_MAHASISWA,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registered: UserProfile? = null,
)

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, error = null) }

    fun onRoleChange(value: String) = _uiState.update { it.copy(role = value, error = null) }

    fun register() {
        val state = _uiState.value
        val name = state.name.trim()
        val email = state.email.trim()
        val error = when {
            name.isEmpty() -> "Nama wajib diisi"
            email.isEmpty() -> "Email wajib diisi"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid"
            state.password.length < 6 -> "Password minimal 6 karakter"
            state.password != state.confirmPassword -> "Konfirmasi password tidak sama"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.register(name, email, state.password, state.role)
                .onSuccess { profile -> _uiState.update { it.copy(isLoading = false, registered = profile) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
