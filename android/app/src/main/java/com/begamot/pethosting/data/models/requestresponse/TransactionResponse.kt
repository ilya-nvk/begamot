package com.begamot.pethosting.data.models.requestresponse

data class TransactionResponse(
    val id: String,
    val listingId: String,
    val payerId: String,
    val receiverId: String,
    val amount: Double,
    val status: String,
    val createdAt: Long
)
