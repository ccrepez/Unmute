package com.unmute.app.data.model

import kotlinx.serialization.Serializable

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
