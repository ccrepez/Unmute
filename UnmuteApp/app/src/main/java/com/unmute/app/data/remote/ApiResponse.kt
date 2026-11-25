package com.unmute.app.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val message: String = "",
)
