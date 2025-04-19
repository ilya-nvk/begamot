package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.ListingStatus
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.data.models.requestresponse.CreateListingRequest
import com.begamot.pethosting.data.models.requestresponse.ListingDetailResponse
import com.begamot.pethosting.data.models.requestresponse.ListingResponse
import com.begamot.pethosting.data.models.requestresponse.PetResponse
import com.begamot.pethosting.data.models.requestresponse.UpdateListingRequest
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import com.begamot.pethosting.data.repositories.ListingRepository
import com.begamot.pethosting.domain.models.ListingDetail
import javax.inject.Inject

class ListingRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ListingRepository {
    
    override suspend fun getAllListings(filters: Map<String, Any>?): Result<List<Listing>> {
        return try {
            val queryParams = filters?.mapValues { it.value.toString() } ?: emptyMap()
            val response = apiService.getListings(queryParams)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toListing() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get listings"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getListingsByOwnerId(ownerId: String): Result<List<Listing>> {
        return getAllListings(mapOf("ownerId" to ownerId))
    }
    
    override suspend fun getListingById(listingId: String): Result<ListingDetail> {
        return try {
            val response = apiService.getListingById(listingId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toListingDetail())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get listing"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createListing(listing: Listing): Result<Listing> {
        return try {
            val request = CreateListingRequest(
                petId = listing.petId,
                title = listing.title,
                description = listing.description,
                startDate = listing.startDate,
                endDate = listing.endDate,
                price = listing.price
            )
            val response = apiService.createListing(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toListing())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create listing"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateListing(listing: Listing): Result<Listing> {
        return try {
            val request = UpdateListingRequest(
                petId = listing.petId,
                title = listing.title,
                description = listing.description,
                startDate = listing.startDate,
                endDate = listing.endDate,
                price = listing.price,
                status = listing.status.name
            )
            val response = apiService.updateListing(listing.id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toListing())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update listing"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteListing(listingId: String): Result<Boolean> {
        return try {
            val response = apiService.deleteListing(listingId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete listing"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper extension functions
    private fun ListingResponse.toListing(): Listing {
        return Listing(
            id = id,
            ownerId = ownerId,
            title = title,
            description = description,
            petId = petId,
            startDate = startDate,
            endDate = endDate,
            price = price,
            status = ListingStatus.valueOf(status),
            createdAt = createdAt
        )
    }
    
    private fun ListingDetailResponse.toListingDetail(): ListingDetail {
        return ListingDetail(
            listing = listing.toListing(),
            pet = pet.toPet(),
            owner = owner.toUser()
        )
    }
    
    private fun PetResponse.toPet(): Pet {
        return Pet(
            id = id,
            ownerId = ownerId,
            name = name,
            type = type,
            breed = breed,
            age = age,
            description = description,
            imageUrls = imageUrls
        )
    }
    
    private fun UserResponse.toUser(): User {
        return User(
            id = id,
            fullName = fullName,
            email = email,
            phone = phone,
            profileImageUrl = profileImageUrl,
            isVerified = isVerified,
            rating = rating,
            reviewCount = reviewCount
        )
    }
}
