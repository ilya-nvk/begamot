package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Transaction
import com.begamot.pethosting.data.models.TransactionStatus
import com.begamot.pethosting.data.models.requestresponse.CreatePaymentIntentRequest
import com.begamot.pethosting.data.models.requestresponse.ProcessPaymentRequest
import com.begamot.pethosting.data.models.requestresponse.TransactionResponse
import com.begamot.pethosting.data.repositories.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : PaymentRepository {
    
    override suspend fun createPaymentIntent(amount: Double, currency: String): Result<String> {
        return try {
            val request = CreatePaymentIntentRequest(
                amount = amount,
                currency = currency
            )
            val response = apiService.createPaymentIntent(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clientSecret)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create payment intent"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processPayment(paymentIntentId: String, transaction: Transaction): Result<Transaction> {
        return try {
            val request = ProcessPaymentRequest(
                listingId = transaction.listingId,
                paymentIntentId = paymentIntentId,
                amount = transaction.amount
            )
            val response = apiService.processPayment(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toTransaction())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to process payment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refundPayment(transactionId: String): Result<Transaction> {
        return try {
            val response = apiService.refundPayment(transactionId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toTransaction())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to refund payment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper extension functions
    private fun TransactionResponse.toTransaction(): Transaction {
        return Transaction(
            id = id,
            listingId = listingId,
            payerId = payerId,
            receiverId = receiverId,
            amount = amount,
            status = TransactionStatus.valueOf(status),
            createdAt = createdAt
        )
    }
}
