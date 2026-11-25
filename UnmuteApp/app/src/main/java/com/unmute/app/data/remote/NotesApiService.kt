package com.unmute.app.data.remote

import com.unmute.app.data.model.SessionNotes
import retrofit2.http.GET
import retrofit2.http.Path

interface NotesApiService {
    @GET("sessions/{id}/notes")
    suspend fun getSessionNotes(@Path("id") sessionId: String): ApiResponse<SessionNotes>
}
