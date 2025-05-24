package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.api.WebSocketService
import com.begamot.pethosting.data.models.requestresponse.ConversationResponse
import com.begamot.pethosting.data.models.requestresponse.MessageResponse
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MessageRepositoryImplTest {

    private val apiService: ApiService = mock()
    private val tokenManager: TokenManager = mock()
    private val webSocketService: WebSocketService = mock()
    private val getUserByIdUseCase: GetUserByIdUseCase = mock()
    private lateinit var repository: MessageRepositoryImpl

    @BeforeEach
    fun setup() {
        whenever(tokenManager.getUserId()).thenReturn("me")
        repository =
            MessageRepositoryImpl(apiService, tokenManager, webSocketService, getUserByIdUseCase)
    }

    @Test
    fun `getConversationBetweenUsers returns mapped messages and caches them`() = runBlocking {
        val msgResp = MessageResponse(
            id = "m1",
            senderId = "a",
            receiverId = "b",
            text = "hello",
            timestamp = 123L,
            isRead = false
        )
        whenever(apiService.getMessages("b"))
            .thenReturn(Response.success(listOf(msgResp)))


        val result1 = repository.getConversationBetweenUsers("a", "b")
        assertTrue(result1.isSuccess)
        val msgs1 = result1.getOrThrow()
        assertEquals(1, msgs1.size)
        with(msgs1[0]) {
            assertEquals("m1", id)
            assertEquals("a", senderId)
            assertEquals("b", receiverId)
            assertEquals("hello", text)
            assertEquals(123L, timestamp)
            assertFalse(isRead)
        }


        reset(apiService)
        val result2 = repository.getConversationBetweenUsers("a", "b")
        verify(apiService, never()).getMessages(any())
        assertEquals(msgs1, result2.getOrThrow())
    }

    @Test
    fun `getAllConversationsForUser returns mapped conversations and caches them`() = runBlocking {
        val userResp = UserResponse(
            id = "u1",
            fullName = "User One",
            email = "u1@example.com",
            phone = "000",
            profileImageUrl = null,
            isVerified = true,
            rating = 5.0f,
            reviewCount = 2
        )
        val msgResp = MessageResponse(
            id = "m2",
            senderId = "x",
            receiverId = "u1",
            text = "hey",
            timestamp = 200L,
            isRead = true
        )
        val convResp = ConversationResponse(userResp, msgResp)
        whenever(apiService.getConversations())
            .thenReturn(Response.success(listOf(convResp)))


        val result1 = repository.getAllConversationsForUser("me")
        assertTrue(result1.isSuccess)
        val convs1 = result1.getOrThrow()
        assertEquals(1, convs1.size)
        with(convs1[0]) {
            assertEquals("u1", user.id)
            assertEquals("hey", lastMessage.text)
            assertEquals("me", currentUserId)
        }


        reset(apiService)
        val result2 = repository.getAllConversationsForUser("me")
        verify(apiService, never()).getConversations()
        assertEquals(convs1, result2.getOrThrow())
    }
}
