package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.data.models.requestresponse.ConversationResponse
import com.begamot.pethosting.data.models.requestresponse.MessageResponse
import com.begamot.pethosting.data.models.requestresponse.SendMessageRequest
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import com.begamot.pethosting.data.repositories.MessageRepository
import com.begamot.pethosting.ui.messages.Conversation
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : MessageRepository {
    
    override suspend fun getConversationBetweenUsers(userId1: String, userId2: String): Result<List<Message>> {
        return try {
            // Assume we're always userId1 and we want messages with userId2
            val response = apiService.getMessages(userId2)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toMessage() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllConversationsForUser(userId: String): Result<List<Conversation>> {
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toConversation() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get conversations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            val request = SendMessageRequest(
                text = message.text
            )
            val response = apiService.sendMessage(message.receiverId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toMessage())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markMessagesAsRead(senderId: String, receiverId: String): Result<Boolean> {
        return try {
            val response = apiService.markMessagesAsRead(senderId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to mark messages as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper extension functions
    private fun MessageResponse.toMessage(): Message {
        return Message(
            id = id,
            senderId = senderId,
            receiverId = receiverId,
            text = text,
            timestamp = timestamp,
            isRead = isRead
        )
    }
    
    private fun ConversationResponse.toConversation(): Conversation {
        val currentUserId = tokenManager.getUserId() ?: ""
        return Conversation(
            user = user.toUser(),
            lastMessage = lastMessage.toMessage(),
            currentUserId = currentUserId
        )
    }
    
    private fun UserResponse.toUser(): User {
        return User(
            id = id,
            fullName = fullName,
            email = email,
            phone = phone,
            profileImageUrl = profileImageUrl,
            isVerified = isVerified,
            rating = rating,
            reviewCount = reviewCount
        )
    }
}
