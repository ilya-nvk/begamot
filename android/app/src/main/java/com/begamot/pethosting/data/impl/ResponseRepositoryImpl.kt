package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.ResponseModel
import com.begamot.pethosting.data.models.ResponseStatus
import com.begamot.pethosting.data.models.requestresponse.CreateResponseRequest
import com.begamot.pethosting.data.models.requestresponse.ResponseResponse
import com.begamot.pethosting.data.models.requestresponse.UpdateResponseStatusRequest
import com.begamot.pethosting.data.repositories.ResponseRepository
import javax.inject.Inject

class ResponseRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ResponseRepository {

    override suspend fun getResponsesByListingId(listingId: String): Result<List<ResponseModel>> {
        return try {
            val response = apiService.getListingResponses(listingId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toResponseModel() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get responses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getResponsesByResponderId(responderId: String): Result<List<ResponseModel>> {
        return try {
            // Этот метод должен получать от API все отклики определенного пользователя
            val allResponses = apiService.getListingResponses("all").body() ?: emptyList()
            val filteredResponses = allResponses.filter { it.responderId == responderId }
            Result.success(filteredResponses.map { it.toResponseModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createResponse(response: ResponseModel): Result<ResponseModel> {
        return try {
            val request = CreateResponseRequest(
                message = response.message
            )
            val apiResponse = apiService.createResponse(response.listingId, request)
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                Result.success(apiResponse.body()!!.toResponseModel())
            } else {
                Result.failure(Exception(apiResponse.errorBody()?.string() ?: "Failed to create response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateResponseStatus(responseId: String, status: ResponseStatus): Result<ResponseModel> {
        return try {
            val parts = responseId.split("_")
            if (parts.size != 2) {
                return Result.failure(Exception("Invalid response ID format"))
            }

            val listingId = parts[0]
            val actualResponseId = parts[1]

            val request = UpdateResponseStatusRequest(
                status = status.name
            )
            val response = apiService.updateResponseStatus(listingId, actualResponseId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toResponseModel())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update response status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper extension functions
    private fun ResponseResponse.toResponseModel(): ResponseModel {
        return ResponseModel(
            id = "${listingId}_$id", // Composite ID for easier reference
            listingId = listingId,
            responderId = responderId,
            message = message,
            status = ResponseStatus.valueOf(status),
            createdAt = createdAt
        )
    }
}
