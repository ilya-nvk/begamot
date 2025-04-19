package com.begamot.pethosting.data.repositories

import com.begamot.pethosting.data.models.ResponseModel
import com.begamot.pethosting.data.models.ResponseStatus
import com.begamot.pethosting.data.models.requestresponse.ListingResponse

interface ResponseRepository {
    suspend fun getResponsesByListingId(listingId: String): Result<List<ResponseModel>>
    suspend fun getResponsesByResponderId(responderId: String): Result<List<ResponseModel>>
    suspend fun createResponse(response: ResponseModel): Result<ResponseModel>
    suspend fun updateResponseStatus(responseId: String, status: ResponseStatus): Result<ResponseModel>
}
