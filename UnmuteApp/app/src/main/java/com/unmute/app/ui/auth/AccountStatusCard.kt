package com.unmute.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unmute.app.data.model.UserProfile
import com.unmute.app.data.repository.AuthRepository
import com.unmute.app.data.repository.CurrentUserStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {
    val profile: StateFlow<UserProfile?> = CurrentUserStore.profile

    fun restore() {
        if (profile.value != null || !repository.isSignedIn) return
        viewModelScope.launch { repository.restoreSession() }
    }

    fun logout() = repository.logout()
}

@Composable
fun AccountStatusCard(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = viewModel(),
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.restore() }

    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val current = profile
            if (current == null) {
                Text("Belum login", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Masuk / Daftar") }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(current.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${current.email} · ${if (current.isLecturer) "Dosen" else "Mahasiswa"}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    OutlinedButton(onClick = viewModel::logout) { Text("Keluar") }
                }
            }
        }
    }
}
