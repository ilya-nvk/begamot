package com.begamot.pethosting.data.models

data class ResponseModel(
    val id: String = "",
    val listingId: String = "",
    val responderId: String = "",
    val message: String = "",
    val status: ResponseStatus = ResponseStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
