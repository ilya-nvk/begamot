package com.begamot.pethosting.ui.messages

import com.begamot.pethosting.data.api.WebSocketService
import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.GetUserConversationsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesViewModelTest {

    private val getUserConversationsUseCase: GetUserConversationsUseCase = mock()
    private val webSocketService: WebSocketService = mock()
    private lateinit var viewModel: MessagesViewModel

    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = MessagesViewModel(getUserConversationsUseCase, webSocketService)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should call connect on webSocketService`() = runTest {
        verify(webSocketService).connect()
    }

    @Test
    fun `loadConversations success updates flows correctly`() = runTest {
        val dummyUser = User(
            id = "u1",
            fullName = "Alice",
            email = "a@a.com",
            phone = "123",
            profileImageUrl = null,
            isVerified = true,
            rating = 4.5f,
            reviewCount = 2
        )
        val dummyMessage = Message(
            id = "m1",
            senderId = "u1",
            receiverId = "u2",
            text = "hi",
            timestamp = 100L,
            isRead = false
        )
        val conv = Conversation(
            user = dummyUser,
            lastMessage = dummyMessage,
            currentUserId = "u2"
        )
        whenever(getUserConversationsUseCase()).thenReturn(Result.success(listOf(conv)))

        viewModel.loadConversations()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        val list = viewModel.conversations.value
        assertEquals(1, list.size)
        assertEquals(conv, list[0])
    }

    @Test
    fun `loadConversations failure updates error`() = runTest {
        whenever(getUserConversationsUseCase())
            .thenReturn(Result.failure(Exception("Network error")))

        viewModel.loadConversations()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Network error", viewModel.error.value)
        assertTrue(viewModel.conversations.value.isEmpty())
    }
}
