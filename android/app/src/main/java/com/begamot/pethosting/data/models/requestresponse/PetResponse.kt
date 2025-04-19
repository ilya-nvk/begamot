package com.begamot.pethosting.data.models.requestresponse

data class PetResponse(
    val id: String,
    val ownerId: String,
    val name: String,
    val type: String,
    val breed: String,
    val age: Int,
    val description: String,
    val imageUrls: List<String>
)
