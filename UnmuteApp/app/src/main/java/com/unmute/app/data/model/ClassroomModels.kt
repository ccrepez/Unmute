package com.unmute.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateClassroomRequest(
    val name: String,
    val courseCode: String,
)

@Serializable
data class CreateClassroomResponse(
    val id: String,
    val joinCode: String,
)

@Serializable
data class JoinClassroomRequest(
    val joinCode: String,
)

@Serializable
data class JoinClassroomResponse(
    val classroomId: String,
)
