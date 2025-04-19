package com.begamot.pethosting.data.models.requestresponse

data class CreatePetRequest(
    val name: String,
    val type: String,
    val breed: String,
    val age: Int,
    val description: String
)
