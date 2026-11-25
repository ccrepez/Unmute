package com.unmute.app.data.repository

import com.unmute.app.data.model.SessionNotes
import com.unmute.app.data.remote.NotesApiService
import com.unmute.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepository(
    private val api: NotesApiService = RetrofitClient.notesApi,
) {
    suspend fun getSessionNotes(sessionId: String): Result<SessionNotes> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getSessionNotes(sessionId)
            response.data ?: error(response.message.ifBlank { "Catatan tidak ditemukan" })
        }
    }
}
