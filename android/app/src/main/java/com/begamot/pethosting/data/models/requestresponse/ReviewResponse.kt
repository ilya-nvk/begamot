package com.begamot.pethosting.data.models.requestresponse

data class ReviewResponse(
    val id: String,
    val listingId: String,
    val reviewerId: String,
    val receiverId: String,
    val rating: Float,
    val comment: String,
    val createdAt: Long
)
