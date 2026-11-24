package com.unmute.app.data.model

import kotlinx.serialization.Serializable

/** Balasan GET /api/sessions/:id/notes. */
@Serializable
data class SessionNotes(
    val title: String,
    val transcript: String,
    val captionCount: Int,
    val questions: List<NoteQuestion> = emptyList(),
)

@Serializable
data class NoteQuestion(
    val text: String,
    val isAnswered: Boolean,
)
