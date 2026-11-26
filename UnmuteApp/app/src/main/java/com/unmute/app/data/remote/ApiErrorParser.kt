package com.unmute.app.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException

object ApiErrorParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun message(error: Throwable, fallback: String = "Terjadi kesalahan, coba lagi"): String = when (error) {
        is HttpException -> fromHttp(error) ?: fallback
        is IOException -> "Tidak bisa terhubung ke server. Cek koneksi internet dan pastikan server menyala"
        else -> error.message?.takeIf { it.isNotBlank() } ?: fallback
    }

    private fun fromHttp(error: HttpException): String? = runCatching {
        val body = error.response()?.errorBody()?.string().orEmpty()
        json.parseToJsonElement(body).jsonObject["message"]?.jsonPrimitive?.content
    }.getOrNull()?.takeIf { it.isNotBlank() }
}
