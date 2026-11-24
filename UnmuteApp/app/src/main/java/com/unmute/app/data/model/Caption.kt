package com.unmute.app.data.model

/** Satu dokumen di sessions/{sessionId}/captions. */
data class Caption(
    val id: String,
    val seq: Long,
    val text: String,
)

/** Dokumen sessions/{sessionId}. */
data class LiveSession(
    val title: String,
    val status: String, // LIVE / ENDED
    val classroomId: String,
)
