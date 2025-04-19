package com.begamot.pethosting.data.models.requestresponse

data class UpdatePetRequest(
    val name: String,
    val type: String,
    val breed: String,
    val age: Int,
    val description: String
)
