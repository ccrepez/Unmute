package com.unmute.app.data.remote

import com.unmute.app.data.model.CreateClassroomRequest
import com.unmute.app.data.model.CreateClassroomResponse
import com.unmute.app.data.model.JoinClassroomRequest
import com.unmute.app.data.model.JoinClassroomResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ClassroomApiService {
    @POST("classrooms")
    suspend fun createClassroom(@Body body: CreateClassroomRequest): ApiResponse<CreateClassroomResponse>

    @POST("classrooms/join")
    suspend fun joinClassroom(@Body body: JoinClassroomRequest): ApiResponse<JoinClassroomResponse>
}
