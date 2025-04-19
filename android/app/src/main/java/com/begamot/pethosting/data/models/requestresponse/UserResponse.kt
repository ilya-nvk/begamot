package com.begamot.pethosting.data.models.requestresponse

data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val profileImageUrl: String?,
    val isVerified: Boolean,
    val rating: Float,
    val reviewCount: Int
)
