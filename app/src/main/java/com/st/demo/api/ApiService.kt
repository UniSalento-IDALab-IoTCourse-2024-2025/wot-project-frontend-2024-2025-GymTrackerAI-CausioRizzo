package com.st.demo.api

import com.st.demo.model.RegisterRequest
import com.st.demo.model.LoginRequest
import com.st.demo.model.ApiResponse
import com.st.demo.model.LoginResponse
import com.st.demo.model.ResetPasswordRequest
import com.st.demo.model.WorkoutSessionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse>

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse>

    @POST("/workout/save")
    suspend fun saveWorkout(@Header("Authorization") token: String, @Body request: WorkoutSessionRequest): Response<WorkoutSessionRequest>

    @DELETE("/workout/deleteByDate")
    suspend fun deleteWorkoutByDate(@Header("Authorization") token: String, @Query("date") date: String): Response<Void>

}