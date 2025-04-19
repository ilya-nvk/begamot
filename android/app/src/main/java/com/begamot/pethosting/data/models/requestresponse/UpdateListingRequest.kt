package com.begamot.pethosting.data.models.requestresponse

data class UpdateListingRequest(
    val petId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val price: Double,
    val status: String
)
