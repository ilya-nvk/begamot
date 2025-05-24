package com.begamot.pethosting.ui.profile

import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.GetCurrentUserUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserListingsUseCase
import com.begamot.pethosting.domain.usecases.GetUserPetsUseCase
import com.begamot.pethosting.domain.usecases.GetUserReviewsUseCase
import com.begamot.pethosting.domain.usecases.UpdateUserUseCase
import com.begamot.pethosting.domain.usecases.UploadUserProfileImageUseCase
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
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val getCurrentUserUseCase: GetCurrentUserUseCase = mock()
    private val getUserByIdUseCase: GetUserByIdUseCase = mock()
    private val updateUserUseCase: UpdateUserUseCase = mock()
    private val uploadUserProfileImageUseCase: UploadUserProfileImageUseCase = mock()
    private val getUserPetsUseCase: GetUserPetsUseCase = mock()
    private val getUserListingsUseCase: GetUserListingsUseCase = mock()
    private val getUserReviewsUseCase: GetUserReviewsUseCase = mock()

    private lateinit var viewModel: ProfileViewModel
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProfileViewModel(
            getCurrentUserUseCase,
            getUserByIdUseCase,
            updateUserUseCase,
            uploadUserProfileImageUseCase,
            getUserPetsUseCase,
            getUserListingsUseCase,
            getUserReviewsUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserProfile success updates user and clears error`() = runTest {
        val dummy = User("u1", "Alice", "a@a", "123", null, true, 5f, 2)
        whenever(getCurrentUserUseCase()).thenReturn(Result.success(dummy))

        viewModel.loadUserProfile()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(dummy, viewModel.user.value)
    }

    @Test
    fun `loadUserProfile failure sets error`() = runTest {
        whenever(getCurrentUserUseCase()).thenReturn(Result.failure(Exception("Auth failed")))

        viewModel.loadUserProfile()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Auth failed", viewModel.error.value)
        assertNull(viewModel.user.value)
    }

    @Test
    fun `loadUserPets success updates pets`() = runTest {
        val petsList = listOf(Pet("p1", "u1", "Name", "type", "breed", 1, "desc", emptyList()))
        whenever(getUserPetsUseCase()).thenReturn(Result.success(petsList))

        viewModel.loadUserPets()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals(petsList, viewModel.pets.value)
    }

    @Test
    fun `updateProfile success updates user`() = runTest {
        val updated = User("u2", "Bob", "b@b", "456", null, false, 0f, 0)
        whenever(updateUserUseCase(updated)).thenReturn(Result.success(updated))

        viewModel.updateProfile(updated)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(updated, viewModel.user.value)
    }

    @Test
    fun `getUserById returns flow with fetched user`() = runTest {
        val other = User("u3", "Carol", "c@c", "789", null, true, 4f, 1)
        whenever(getUserByIdUseCase("u3")).thenReturn(Result.success(other))

        val flow = viewModel.getUserById("u3")
        advanceUntilIdle()

        assertEquals(other, flow.value)
    }
}
