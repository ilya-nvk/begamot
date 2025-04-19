package com.begamot.pethosting.data.models.requestresponse

data class ResponseResponse(
    val id: String,
    val listingId: String,
    val responderId: String,
    val message: String,
    val status: String,
    val createdAt: Long
)
