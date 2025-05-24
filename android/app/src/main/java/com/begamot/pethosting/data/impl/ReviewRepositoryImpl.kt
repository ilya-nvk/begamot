package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Review
import com.begamot.pethosting.data.models.requestresponse.CreateReviewRequest
import com.begamot.pethosting.data.models.requestresponse.ReviewResponse
import com.begamot.pethosting.data.repositories.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ReviewRepository {
    
    override suspend fun getReviewsByReceiverId(receiverId: String): Result<List<Review>> {
        return try {
            val response = apiService.getUserReviews(receiverId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toReview() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get reviews"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createReview(review: Review): Result<Review> {
        return try {
            val request = CreateReviewRequest(
                listingId = review.listingId,
                receiverId = review.receiverId,
                rating = review.rating,
                comment = review.comment
            )
            val response = apiService.createReview(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toReview())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create review"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ReviewResponse.toReview(): Review {
        return Review(
                id = id,
                listingId = listingId,
                reviewerId = reviewerId,
                receiverId = receiverId,
                rating = rating,
                comment = comment,
                createdAt = createdAt
        )
    }
}
