package com.begamot.pethosting.data.models.requestresponse

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String
)
