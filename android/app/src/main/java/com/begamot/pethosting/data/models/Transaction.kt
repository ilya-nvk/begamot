package com.begamot.pethosting.data.models

data class Transaction(
    val id: String = "",
    val listingId: String = "",
    val payerId: String = "",
    val receiverId: String = "",
    val amount: Double = 0.0,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

