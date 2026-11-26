package com.unmute.app.data.repository

import com.unmute.app.data.model.CreateClassroomRequest
import com.unmute.app.data.model.CreateClassroomResponse
import com.unmute.app.data.model.JoinClassroomRequest
import com.unmute.app.data.model.JoinClassroomResponse
import com.unmute.app.data.remote.ApiErrorParser
import com.unmute.app.data.remote.ClassroomApiService
import com.unmute.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClassroomRepository(
    private val api: ClassroomApiService = RetrofitClient.classroomApi,
) {
    suspend fun createClassroom(name: String, courseCode: String): Result<CreateClassroomResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.createClassroom(CreateClassroomRequest(name = name, courseCode = courseCode))
                response.data ?: error(response.message.ifBlank { "Gagal membuat kelas" })
            }.recoverCatching { e ->
                throw Exception(ApiErrorParser.message(e, "Gagal membuat kelas"))
            }
        }

    suspend fun joinClassroom(joinCode: String): Result<JoinClassroomResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.joinClassroom(JoinClassroomRequest(joinCode = joinCode))
                response.data ?: error(response.message.ifBlank { "Gagal bergabung ke kelas" })
            }.recoverCatching { e ->
                throw Exception(ApiErrorParser.message(e, "Gagal bergabung ke kelas"))
            }
        }
}
