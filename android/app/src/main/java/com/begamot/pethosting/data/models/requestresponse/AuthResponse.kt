package com.begamot.pethosting.data.models.requestresponse

data class AuthResponse(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)
