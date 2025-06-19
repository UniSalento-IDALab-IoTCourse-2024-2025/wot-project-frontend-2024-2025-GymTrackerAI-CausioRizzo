package com.st.demo.model

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String,
    val confirmPassword: String
)