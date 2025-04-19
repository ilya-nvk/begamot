package com.begamot.pethosting.data.repositories

import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.ui.messages.Conversation

interface MessageRepository {
    suspend fun getConversationBetweenUsers(userId1: String, userId2: String): Result<List<Message>>
    suspend fun getAllConversationsForUser(userId: String): Result<List<Conversation>>
    suspend fun sendMessage(message: Message): Result<Message>
    suspend fun markMessagesAsRead(senderId: String, receiverId: String): Result<Boolean>
}
