package com.begamot.pethosting.data.models.requestresponse

data class CreatePaymentIntentRequest(
    val amount: Double,
    val currency: String
)
