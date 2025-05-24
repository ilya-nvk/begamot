package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryImplTest {

    private val apiService: ApiService = mock()
    private val tokenManager: TokenManager = mock()
    private lateinit var repository: UserRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = UserRepositoryImpl(apiService, tokenManager)
    }

    @Test
    fun `getUserById returns User on successful API call`() = runBlocking {
        val resp = UserResponse(
            id = "u1",
            fullName = "Alice",
            email = "alice@example.com",
            phone = "123",
            profileImageUrl = "img.png",
            isVerified = true,
            rating = 4.2f,
            reviewCount = 10
        )
        whenever(apiService.getUserById("u1"))
            .thenReturn(Response.success(resp))

        val result = repository.getUserById("u1")
        assertTrue(result.isSuccess)
        val user = result.getOrThrow()
        assertEquals("u1", user.id)
        assertEquals("Alice", user.fullName)
        assertEquals("alice@example.com", user.email)
        assertEquals("123", user.phone)
        assertEquals("img.png", user.profileImageUrl)
        assertTrue(user.isVerified)
        assertEquals(4.2f, user.rating)
        assertEquals(10, user.reviewCount)
    }

    @Test
    fun `getCurrentUser returns failure when no token`() = runBlocking {
        whenever(tokenManager.getUserId()).thenReturn(null)

        val result = repository.getCurrentUser()
        assertTrue(result.isFailure)
    }

    @Test
    fun `getCurrentUser delegates to getUserById when token present`() = runBlocking {
        whenever(tokenManager.getUserId()).thenReturn("u3")
        val resp = UserResponse(
            id = "u3",
            fullName = "Bob",
            email = "bob@example.com",
            phone = "456",
            profileImageUrl = null,
            isVerified = false,
            rating = 0f,
            reviewCount = 0
        )
        whenever(apiService.getUserById("u3"))
            .thenReturn(Response.success(resp))

        val result = repository.getCurrentUser()
        assertTrue(result.isSuccess)
        assertEquals("Bob", result.getOrThrow().fullName)
    }

    @Test
    fun `logoutUser clears tokens`() = runBlocking {
        repository.logoutUser()
        verify(tokenManager).clearTokens()
    }
}
