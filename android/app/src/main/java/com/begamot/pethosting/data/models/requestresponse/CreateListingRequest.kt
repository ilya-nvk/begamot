package com.begamot.pethosting.data.models.requestresponse

data class CreateListingRequest(
    val petId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val price: Double
)
