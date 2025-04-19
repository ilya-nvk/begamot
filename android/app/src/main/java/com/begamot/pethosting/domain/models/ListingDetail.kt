package com.begamot.pethosting.domain.models

import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.User

/**
 * Represents detailed listing information including the listing itself,
 * the associated pet, and the owner.
 */
data class ListingDetail(
    val listing: Listing,
    val pet: Pet,
    val owner: User
)
