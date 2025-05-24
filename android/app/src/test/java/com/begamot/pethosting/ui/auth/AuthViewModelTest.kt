package com.begamot.pethosting.ui.auth

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.LoginUseCase
import com.begamot.pethosting.domain.usecases.LogoutUseCase
import com.begamot.pethosting.domain.usecases.RegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val loginUseCase: LoginUseCase = mock()
    private val registerUseCase: RegisterUseCase = mock()
    private val logoutUseCase: LogoutUseCase = mock()
    private val tokenManager: TokenManager = mock()

    private lateinit var viewModel: AuthViewModel
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        whenever(tokenManager.isLoggedIn()).thenReturn(false)
        viewModel = AuthViewModel(loginUseCase, registerUseCase, logoutUseCase, tokenManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates state and isUserLoggedIn`() = runTest {
        whenever(loginUseCase("email", "pass")).thenReturn(Result.success(User()))

        viewModel.login("email", "pass")
        advanceUntilIdle()

        val state = viewModel.loginState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertTrue(viewModel.isUserLoggedIn.value)
    }

    @Test
    fun `register success updates registerState`() = runTest {
        val dummyUser = User(
            id = "u",
            fullName = "Name",
            email = "e",
            phone = "p",
            profileImageUrl = null,
            isVerified = false,
            rating = 0f,
            reviewCount = 0
        )
        whenever(registerUseCase("e", "p", dummyUser, null))
            .thenReturn(Result.success(dummyUser))

        viewModel.register("e", "p", dummyUser, null)
        advanceUntilIdle()

        val state = viewModel.registerState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
    }

    @Test
    fun `logout sets isUserLoggedIn to false`() = runTest {
        whenever(tokenManager.isLoggedIn()).thenReturn(true)
        viewModel = AuthViewModel(loginUseCase, registerUseCase, logoutUseCase, tokenManager)

        viewModel.logout()
        advanceUntilIdle()

        assertFalse(viewModel.isUserLoggedIn.value)
        verify(logoutUseCase).invoke()
    }

    @Test
    fun `resetLoginState clears loginState`() {

        viewModel.resetLoginState()
        val state = viewModel.loginState.value
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.error)
    }
}
