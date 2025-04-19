package com.begamot.pethosting.domain.usecases

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.ResponseModel
import com.begamot.pethosting.data.models.ResponseStatus
import com.begamot.pethosting.data.repositories.ResponseRepository
import javax.inject.Inject

class GetListingResponsesUseCase @Inject constructor(
    private val responseRepository: ResponseRepository
) {
    suspend operator fun invoke(listingId: String): Result<List<ResponseModel>> {
        return responseRepository.getResponsesByListingId(listingId)
    }
}

class GetUserResponsesUseCase @Inject constructor(
    private val responseRepository: ResponseRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<List<ResponseModel>> {
        val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return responseRepository.getResponsesByResponderId(userId)
    }
}

class CreateResponseUseCase @Inject constructor(
    private val responseRepository: ResponseRepository
) {
    suspend operator fun invoke(response: ResponseModel): Result<ResponseModel> {
        return responseRepository.createResponse(response)
    }
}

class UpdateResponseStatusUseCase @Inject constructor(
    private val responseRepository: ResponseRepository
) {
    suspend operator fun invoke(responseId: String, status: ResponseStatus): Result<ResponseModel> {
        return responseRepository.updateResponseStatus(responseId, status)
    }
}
