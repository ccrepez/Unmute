package com.unmute.app.data.remote

import com.unmute.app.data.model.SaveProfileRequest
import com.unmute.app.data.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {
    @POST("users/me")
    suspend fun saveProfile(@Body body: SaveProfileRequest): ApiResponse<UserProfile>

    @GET("users/me")
    suspend fun getProfile(): ApiResponse<UserProfile>
}
