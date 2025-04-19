package com.begamot.pethosting.domain.usecases

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.repositories.ListingRepository
import com.begamot.pethosting.domain.models.ListingDetail
import javax.inject.Inject

class GetAllListingsUseCase @Inject constructor(
    private val listingRepository: ListingRepository
) {
    suspend operator fun invoke(filters: Map<String, Any>? = null): Result<List<Listing>> {
        return listingRepository.getAllListings(filters)
    }
}

class GetUserListingsUseCase @Inject constructor(
    private val listingRepository: ListingRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<List<Listing>> {
        val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return listingRepository.getListingsByOwnerId(userId)
    }
}

class GetListingDetailsUseCase @Inject constructor(
    private val listingRepository: ListingRepository
) {
    suspend operator fun invoke(listingId: String): Result<ListingDetail> {
        return listingRepository.getListingById(listingId)
    }
}

class CreateListingUseCase @Inject constructor(
    private val listingRepository: ListingRepository
) {
    suspend operator fun invoke(listing: Listing): Result<Listing> {
        return listingRepository.createListing(listing)
    }
}

class UpdateListingUseCase @Inject constructor(
    private val listingRepository: ListingRepository
) {
    suspend operator fun invoke(listing: Listing): Result<Listing> {
        return listingRepository.updateListing(listing)
    }
}

class DeleteListingUseCase @Inject constructor(
    private val listingRepository: ListingRepository
) {
    suspend operator fun invoke(listingId: String): Result<Boolean> {
        return listingRepository.deleteListing(listingId)
    }
}
