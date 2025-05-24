package com.begamot.pethosting.ui

import com.begamot.pethosting.data.api.WebSocketService
import com.begamot.pethosting.data.models.Message
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.GetConversationMessagesUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.MarkMessagesAsReadUseCase
import com.begamot.pethosting.domain.usecases.SendMessageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val getUserById: GetUserByIdUseCase = mock()
    private val getConversation: GetConversationMessagesUseCase = mock()
    private val sendMessage: SendMessageUseCase = mock()
    private val markRead: MarkMessagesAsReadUseCase = mock()
    private val webSocket: WebSocketService = mock()

    private lateinit var vm: ChatViewModel
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        whenever(webSocket.connectionState).thenReturn(MutableStateFlow(false))
        vm = ChatViewModel(getUserById, getConversation, sendMessage, markRead, webSocket)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls connect on webSocketService`() {
        verify(webSocket).connect()
    }

    @Test
    fun `loadUserInfo success updates user and clears error and loading`() = runTest {
        val user = User("u", "Name", "e", "p", null, true, 0f, 0)
        whenever(getUserById("u")).thenReturn(Result.success(user))

        vm.loadUserInfo("u")
        advanceUntilIdle()

        assertFalse(vm.isLoading.value)
        assertNull(vm.error.value)
        assertEquals(user, vm.user.value)
    }

    @Test
    fun `loadUserInfo failure sets error`() = runTest {
        whenever(getUserById("x")).thenReturn(Result.failure(Exception("Err")))

        vm.loadUserInfo("x")
        advanceUntilIdle()

        assertFalse(vm.isLoading.value)
        assertEquals("Err", vm.error.value)
        assertNull(vm.user.value)
    }

    @Test
    fun `loadMessages success updates messages, clears error and calls markMessagesAsRead`() =
        runTest {
            val msg1 = Message("m1", "a", "b", "hi", 1L, false)
            whenever(getConversation("b")).thenReturn(Result.success(listOf(msg1)))

            vm.loadMessages("b")
            advanceUntilIdle()

            assertFalse(vm.isLoading.value)
            assertNull(vm.error.value)
            assertEquals(listOf(msg1), vm.messages.value)
            verify(markRead).invoke("b")
        }

    @Test
    fun `loadMessages failure sets error and does not call markMessagesAsRead`() = runTest {
        whenever(getConversation("y")).thenReturn(Result.failure(Exception("Fail")))

        vm.loadMessages("y")
        advanceUntilIdle()

        assertFalse(vm.isLoading.value)
        assertEquals("Fail", vm.error.value)
        assertTrue(vm.messages.value.isEmpty())
        verify(markRead, never()).invoke(any())
    }

    @Test
    fun `sendMessage failure sets error`() = runTest {
        whenever(webSocket.connectionState).thenReturn(MutableStateFlow(true))
        vm = ChatViewModel(getUserById, getConversation, sendMessage, markRead, webSocket)
        whenever(sendMessage("r", "t")).thenReturn(Result.failure(Exception("SM Error")))

        vm.sendMessage("r", "t")
        advanceUntilIdle()

        assertEquals("SM Error", vm.error.value)
    }
}
