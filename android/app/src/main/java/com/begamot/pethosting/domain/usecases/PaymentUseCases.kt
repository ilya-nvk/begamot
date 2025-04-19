package com.begamot.pethosting.domain.usecases

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Transaction
import com.begamot.pethosting.data.repositories.PaymentRepository
import javax.inject.Inject

class CreatePaymentIntentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(amount: Double, currency: String = "USD"): Result<String> {
        return paymentRepository.createPaymentIntent(amount, currency)
    }
}

class ProcessPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(paymentIntentId: String, listingId: String, receiverId: String, amount: Double): Result<Transaction> {
        val payerId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        val transaction = Transaction(
            listingId = listingId,
            payerId = payerId,
            receiverId = receiverId,
            amount = amount
        )
        return paymentRepository.processPayment(paymentIntentId, transaction)
    }
}

class RefundPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(transactionId: String): Result<Transaction> {
        return paymentRepository.refundPayment(transactionId)
    }
}
