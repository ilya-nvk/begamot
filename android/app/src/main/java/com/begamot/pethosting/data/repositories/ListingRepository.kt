package com.begamot.pethosting.data.repositories

import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.domain.models.ListingDetail

interface ListingRepository {
    suspend fun getAllListings(filters: Map<String, Any>? = null): Result<List<Listing>>
    suspend fun getListingsByOwnerId(ownerId: String): Result<List<Listing>>
    suspend fun getListingById(listingId: String): Result<ListingDetail>
    suspend fun createListing(listing: Listing): Result<Listing>
    suspend fun updateListing(listing: Listing): Result<Listing>
    suspend fun deleteListing(listingId: String): Result<Boolean>
}
