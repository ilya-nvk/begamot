package com.begamot.pethosting.ui.listings

import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.ListingStatus
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.CreateListingUseCase
import com.begamot.pethosting.domain.usecases.DeleteListingUseCase
import com.begamot.pethosting.domain.usecases.GetAllListingsUseCase
import com.begamot.pethosting.domain.usecases.GetListingDetailsUseCase
import com.begamot.pethosting.domain.usecases.GetPetByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserListingsUseCase
import com.begamot.pethosting.domain.usecases.UpdateListingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ListingViewModelTest {

    private val getAllListingsUseCase: GetAllListingsUseCase = mock()
    private val getUserListingsUseCase: GetUserListingsUseCase = mock()
    private val getListingDetailsUseCase: GetListingDetailsUseCase = mock()
    private val createListingUseCase: CreateListingUseCase = mock()
    private val updateListingUseCase: UpdateListingUseCase = mock()
    private val deleteListingUseCase: DeleteListingUseCase = mock()
    private val getUserByIdUseCase: GetUserByIdUseCase = mock()
    private val getPetByIdUseCase: GetPetByIdUseCase = mock()

    private lateinit var viewModel: ListingViewModel
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ListingViewModel(
            getAllListingsUseCase,
            getUserListingsUseCase,
            getListingDetailsUseCase,
            createListingUseCase,
            updateListingUseCase,
            deleteListingUseCase,
            getUserByIdUseCase,
            getPetByIdUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadListings success updates listings and clears loading`() = runTest {
        val dummy = Listing(
            id = "1",
            ownerId = "o",
            petId = "p",
            title = "T",
            description = "D",
            startDate = 0L,
            endDate = 1L,
            price = 10.0,
            status = ListingStatus.ACTIVE,
            createdAt = 0L
        )
        whenever(getAllListingsUseCase(mapOf("k" to "v")))
            .thenReturn(Result.success(listOf(dummy)))

        viewModel.loadListings(mapOf("k" to "v"))
        advanceUntilIdle()

        // isLoading toggles off
        assertFalse(viewModel.isLoading.value)
        // listings updated
        val list = viewModel.listings.value
        assertEquals(1, list.size)
        assertEquals("1", list[0].id)
    }

    @Test
    fun `resetActionState returns ActionState Idle`() = runTest {
        // simulate non-idle
        viewModel.createListing(dummyListing())
        advanceUntilIdle()
        viewModel.resetActionState()
        assertTrue(viewModel.actionState.value is ListingViewModel.ActionState.Idle)
    }

    @Test
    fun `getUser returns StateFlow with fetched user`() = runTest {
        val user = User(
            id = "u1",
            fullName = "Alice",
            email = "e",
            phone = "p",
            profileImageUrl = null,
            isVerified = true,
            rating = 5f,
            reviewCount = 2
        )
        whenever(getUserByIdUseCase("u1")).thenReturn(Result.success(user))

        val flow = viewModel.getUser("u1")
        advanceUntilIdle()
        assertEquals(user, flow.first())
    }

    private fun dummyListing() = Listing(
        id = "x", ownerId = "o", petId = "p", title = "",
        description = "", startDate = 0L, endDate = 1L,
        price = 0.0, status = ListingStatus.ACTIVE, createdAt = 0L
    )
}
