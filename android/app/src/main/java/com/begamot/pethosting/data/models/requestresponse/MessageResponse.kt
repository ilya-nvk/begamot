package com.begamot.pethosting.data.models.requestresponse

data class MessageResponse(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long,
    val isRead: Boolean
)
