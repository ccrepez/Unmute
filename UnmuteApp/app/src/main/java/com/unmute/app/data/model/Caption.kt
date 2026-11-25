package com.unmute.app.data.model

data class Caption(
    val id: String,
    val seq: Long,
    val text: String,
)

data class LiveSession(
    val title: String,
    val status: String,
    val classroomId: String,
)
