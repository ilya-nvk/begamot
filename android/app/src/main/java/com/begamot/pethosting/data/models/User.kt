package com.begamot.pethosting.data.models

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0
)
