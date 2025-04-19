package com.begamot.pethosting.data.repositories

import com.begamot.pethosting.data.models.Review

interface ReviewRepository {
    suspend fun getReviewsByReceiverId(receiverId: String): Result<List<Review>>
    suspend fun createReview(review: Review): Result<Review>
}
