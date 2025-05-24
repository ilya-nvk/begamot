package com.begamot.pethosting.data.impl

import android.util.Log
import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.api.WebSocketListener
import com.begamot.pethosting.data.api.WebSocketMessage
import com.begamot.pethosting.data.api.WebSocketService
import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.data.models.requestresponse.ConversationResponse
import com.begamot.pethosting.data.models.requestresponse.MessageResponse
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import com.begamot.pethosting.data.repositories.MessageRepository
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.ui.messages.Conversation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val webSocketService: WebSocketService,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : MessageRepository, WebSocketListener {

    private val _messages = mutableMapOf<String, MutableStateFlow<List<Message>>>()
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        webSocketService.addListener(this)
    }

    override fun onNewMessage(message: Message) {
        
        val userId = if (message.senderId == tokenManager.getUserId()) {
            message.receiverId
        } else {
            message.senderId
        }

        val messagesFlow = _messages[userId]
        if (messagesFlow != null) {
            messagesFlow.value = messagesFlow.value + message
        }

        
        updateConversationWithLastMessage(message)
    }

    override fun onConnectionStateChange(isConnected: Boolean) {
        Log.d("MessageRepository", "WebSocket connection state: $isConnected")
    }

    private fun updateConversationWithLastMessage(message: Message) {
        
        val currentUserId = tokenManager.getUserId() ?: return
        val otherUserId = if (message.senderId == currentUserId) message.receiverId else message.senderId

        scope.launch {
            
            val userResult = getUserByIdUseCase(otherUserId)
            userResult.fold(
                onSuccess = { user ->
                    val updatedConversations = _conversations.value.toMutableList()
                    val existingIndex = updatedConversations.indexOfFirst { it.user.id == otherUserId }

                    if (existingIndex != -1) {
                        
                        updatedConversations[existingIndex] = Conversation(
                            user = updatedConversations[existingIndex].user,
                            lastMessage = message,
                            currentUserId = currentUserId
                        )
                    } else {
                        
                        updatedConversations.add(
                            Conversation(
                                user = user,
                                lastMessage = message,
                                currentUserId = currentUserId
                            )
                        )
                    }

                    
                    updatedConversations.sortByDescending { it.lastMessage.timestamp }
                    _conversations.value = updatedConversations
                },
                onFailure = { /* Игнорируем ошибку */ }
            )
        }
    }

    override suspend fun getConversationBetweenUsers(userId1: String, userId2: String): Result<List<Message>> {
        
        val cacheKey = userId2 
        val cachedMessages = _messages[cacheKey]

        if (cachedMessages != null) {
            return Result.success(cachedMessages.value)
        }

        
        return try {
            val response = apiService.getMessages(userId2)
            if (response.isSuccessful && response.body() != null) {
                val messages = response.body()!!.map { it.toMessage() }

                
                _messages[cacheKey] = MutableStateFlow(messages)

                Result.success(messages)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllConversationsForUser(userId: String): Result<List<Conversation>> {
        
        if (_conversations.value.isNotEmpty()) {
            return Result.success(_conversations.value)
        }

        
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful && response.body() != null) {
                val conversations = response.body()!!.map { it.toConversation() }
                _conversations.value = conversations
                Result.success(conversations)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get conversations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(message: Message): Result<Message> {
        
        val webSocketMessage = WebSocketMessage(
            type = "MESSAGE",
            senderId = message.senderId,
            receiverId = message.receiverId,
            content = message.text
        )

        webSocketService.sendMessage(webSocketMessage)

        
        val cacheKey = message.receiverId
        val messagesFlow = _messages[cacheKey]
        if (messagesFlow != null) {
            messagesFlow.value = messagesFlow.value + message
        } else {
            _messages[cacheKey] = MutableStateFlow(listOf(message))
        }

        
        updateConversationWithLastMessage(message)

        return Result.success(message)
    }

    override suspend fun markMessagesAsRead(senderId: String, receiverId: String): Result<Boolean> {
        
        val webSocketMessage = WebSocketMessage(
            type = "READ_RECEIPT",
            senderId = receiverId, 
            receiverId = senderId, 
            content = "READ"
        )

        webSocketService.sendMessage(webSocketMessage)

        
        val cacheKey = senderId
        val messagesFlow = _messages[cacheKey]
        if (messagesFlow != null) {
            val updatedMessages = messagesFlow.value.map {
                if (it.senderId == senderId && !it.isRead) {
                    it.copy(isRead = true)
                } else {
                    it
                }
            }
            messagesFlow.value = updatedMessages
        }

        return Result.success(true)
    }

    
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
