package com.begamot.pethosting.data.models

data class Listing(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val petId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val price: Double = 0.0,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)

