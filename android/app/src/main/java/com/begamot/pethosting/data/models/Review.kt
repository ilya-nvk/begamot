package com.begamot.pethosting.data.models

data class Review(
    val id: String = "",
    val listingId: String = "",
    val reviewerId: String = "",
    val receiverId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
