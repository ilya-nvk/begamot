package com.begamot.pethosting.data.models.requestresponse

data class ListingResponse(
    val id: String,
    val ownerId: String,
    val petId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val price: Double,
    val status: String,
    val createdAt: Long
)
