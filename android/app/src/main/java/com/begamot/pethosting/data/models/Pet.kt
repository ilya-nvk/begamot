package com.begamot.pethosting.data.models

import java.util.Collections.emptyList

data class Pet(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = "",
    val breed: String = "",
    val age: Int = 0,
    val description: String = "",
    val imageUrls: List<String> = emptyList()
)
