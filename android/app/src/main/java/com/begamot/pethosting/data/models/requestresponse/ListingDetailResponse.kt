package com.begamot.pethosting.data.models.requestresponse

data class ListingDetailResponse(
    val listing: ListingResponse,
    val pet: PetResponse,
    val owner: UserResponse
)
