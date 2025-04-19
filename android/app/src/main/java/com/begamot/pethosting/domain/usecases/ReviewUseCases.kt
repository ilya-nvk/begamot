package com.begamot.pethosting.domain.usecases

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Review
import com.begamot.pethosting.data.repositories.ReviewRepository
import javax.inject.Inject

class GetUserReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Review>> {
        return reviewRepository.getReviewsByReceiverId(userId)
    }
}

class CreateReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(listingId: String, receiverId: String, rating: Float, comment: String): Result<Review> {
        val reviewerId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        val review = Review(
            listingId = listingId,
            reviewerId = reviewerId,
            receiverId = receiverId,
            rating = rating,
            comment = comment
        )
        return reviewRepository.createReview(review)
    }
}
