package com.begamot.pethosting.domain.usecases

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.data.repositories.MessageRepository
import com.begamot.pethosting.ui.messages.Conversation
import javax.inject.Inject

class GetUserConversationsUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<List<Conversation>> {
        val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return messageRepository.getAllConversationsForUser(userId)
    }
}

class GetConversationMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(otherUserId: String): Result<List<Message>> {
        val currentUserId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return messageRepository.getConversationBetweenUsers(currentUserId, otherUserId)
    }
}

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(receiverId: String, text: String): Result<Message> {
        val senderId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        val message = Message(
            senderId = senderId,
            receiverId = receiverId,
            text = text
        )
        return messageRepository.sendMessage(message)
    }
}

class MarkMessagesAsReadUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(senderId: String): Result<Boolean> {
        val receiverId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return messageRepository.markMessagesAsRead(senderId, receiverId)
    }
}
