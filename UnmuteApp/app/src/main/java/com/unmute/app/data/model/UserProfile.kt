package com.unmute.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val firebaseUid: String,
    val name: String,
    val email: String,
    val role: String,
) {
    val isLecturer: Boolean get() = role == ROLE_DOSEN

    companion object {
        const val ROLE_DOSEN = "DOSEN"
        const val ROLE_MAHASISWA = "MAHASISWA"
    }
}

@Serializable
data class SaveProfileRequest(
    val name: String,
    val role: String,
)
