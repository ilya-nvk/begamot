package com.begamot.pethosting.data.models.requestresponse

data class ProcessPaymentRequest(
    val listingId: String,
    val paymentIntentId: String,
    val amount: Double
)
