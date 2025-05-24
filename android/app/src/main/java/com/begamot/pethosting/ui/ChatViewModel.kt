package com.begamot.pethosting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.api.WebSocketService
import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.GetConversationMessagesUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.MarkMessagesAsReadUseCase
import com.begamot.pethosting.domain.usecases.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase,
    private val webSocketService: WebSocketService
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {

        webSocketService.connect()
    }

    fun loadUserInfo(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getUserByIdUseCase(userId)

            result.fold(
                onSuccess = { _user.value = it },
                onFailure = { _error.value = it.message }
            )

            _isLoading.value = false
        }
    }

    fun loadMessages(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getConversationMessagesUseCase(userId)

            result.fold(
                onSuccess = {
                    _messages.value = it
                    markMessagesAsRead(userId)
                },
                onFailure = { _error.value = it.message }
            )

            _isLoading.value = false
        }
    }

    fun sendMessage(receiverId: String, text: String) {
        viewModelScope.launch {

            if (!webSocketService.connectionState.value) {
                webSocketService.connect()
            }

            val result = sendMessageUseCase(receiverId, text)

            result.fold(
                onSuccess = {

                },
                onFailure = { _error.value = it.message }
            )
        }
    }

    private fun markMessagesAsRead(senderId: String) {
        viewModelScope.launch {
            markMessagesAsReadUseCase(senderId)
        }
    }
}
