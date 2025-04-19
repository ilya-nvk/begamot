package com.begamot.pethosting.data.repositories

import com.begamot.pethosting.data.models.Transaction

interface PaymentRepository {
    suspend fun createPaymentIntent(amount: Double, currency: String = "RUB"): Result<String>
    suspend fun processPayment(paymentIntentId: String, transaction: Transaction): Result<Transaction>
    suspend fun refundPayment(transactionId: String): Result<Transaction>
}
