package com.begamot.pethosting.data.models.requestresponse

data class CreateReviewRequest(
    val listingId: String,
    val receiverId: String,
    val rating: Float,
    val comment: String
)
