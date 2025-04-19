package com.begamot.pethosting.data.models.requestresponse

data class ConversationResponse(
    val user: UserResponse,
    val lastMessage: MessageResponse
)
