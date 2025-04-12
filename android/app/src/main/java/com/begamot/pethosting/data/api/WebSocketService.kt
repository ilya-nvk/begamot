package com.begamot.pethosting.data.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.begamot.pethosting.BuildConfig
import com.begamot.pethosting.data.models.Message
import com.google.gson.Gson
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.UUID

class WebSocketMessage(
    val type: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

interface WebSocketListener {
    fun onNewMessage(message: Message)
    fun onConnectionStateChange(isConnected: Boolean)
}

@Singleton
class WebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private val listeners = mutableListOf<WebSocketListener>()
    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    private val webSocketListener = object : okhttp3.WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            super.onOpen(webSocket, response)
            isConnected = true
            _connectionState.value = true
            listeners.forEach { it.onConnectionStateChange(true) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            try {
                val webSocketMessage = gson.fromJson(text, WebSocketMessage::class.java)
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = webSocketMessage.senderId,
                    receiverId = webSocketMessage.receiverId,
                    text = webSocketMessage.content,
                    timestamp = webSocketMessage.timestamp,
                    isRead = false
                )
                listeners.forEach { it.onNewMessage(message) }
            } catch (e: Exception) {
                Log.e("WebSocketService", "Failed to parse message: ${e.message}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            isConnected = false
            _connectionState.value = false
            listeners.forEach { it.onConnectionStateChange(false) }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            super.onFailure(webSocket, t, response)
            isConnected = false
            _connectionState.value = false
            listeners.forEach { it.onConnectionStateChange(false) }
            // Попытка переподключения через 3 секунды
            Handler(Looper.getMainLooper()).postDelayed({
                connect()
            }, 3000)
        }
    }

    fun connect() {
        if (isConnected) return

        val userId = tokenManager.getUserId() ?: return
        val token = tokenManager.getAccessToken() ?: return

        val request = Request.Builder()
            .url("${BuildConfig.WEBSOCKET_BASE_URL}/ws/chat/$userId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        isConnected = false
        _connectionState.value = false
    }

    fun sendMessage(message: WebSocketMessage) {
        if (!isConnected) {
            connect()
        }
        
        val json = gson.toJson(message)
        webSocket?.send(json)
    }

    fun addListener(listener: WebSocketListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: WebSocketListener) {
        listeners.remove(listener)
    }
}
