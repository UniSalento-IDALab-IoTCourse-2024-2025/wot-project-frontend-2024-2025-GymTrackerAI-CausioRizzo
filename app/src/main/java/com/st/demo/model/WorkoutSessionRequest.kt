package com.st.demo.model

data class WorkoutSessionRequest(
    val exerciseType: String,
    val repetitions: Int,
    val timeInSeconds: Long,
    val workoutDate: String
)