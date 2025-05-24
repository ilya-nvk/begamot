package com.begamot.pethosting.ui.payment

import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.ListingStatus
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.Transaction
import com.begamot.pethosting.data.models.TransactionStatus
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.models.ListingDetail
import com.begamot.pethosting.domain.usecases.CreatePaymentIntentUseCase
import com.begamot.pethosting.domain.usecases.GetListingDetailsUseCase
import com.begamot.pethosting.domain.usecases.ProcessPaymentUseCase
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private val getListingDetailsUseCase: GetListingDetailsUseCase = mock()
    private val createPaymentIntentUseCase: CreatePaymentIntentUseCase = mock()
    private val processPaymentUseCase: ProcessPaymentUseCase = mock()

    private lateinit var viewModel: PaymentViewModel
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = PaymentViewModel(
            getListingDetailsUseCase,
            createPaymentIntentUseCase,
            processPaymentUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadListing success sets listing and owner and clears error and loading`() = runTest {
        val dummyListing = Listing(
            id = "L1", ownerId = "O1", petId = "P1",
            title = "T", description = "D", startDate = 0L,
            endDate = 1L, price = 10.0, status = ListingStatus.ACTIVE, createdAt = 0L
        )
        val dummyUser = User(
            id = "O1", fullName = "Owner", email = "o@o", phone = "123",
            profileImageUrl = null, isVerified = true, rating = 5f, reviewCount = 0
        )
        val detail = ListingDetail(
            dummyListing,
            Pet("P1", "O1", "N", "type", "breed", 1, "desc", emptyList()),
            dummyUser
        )
        whenever(getListingDetailsUseCase("L1")).thenReturn(Result.success(detail))

        viewModel.loadListing("L1")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(dummyListing, viewModel.listing.value)
        assertEquals(dummyUser, viewModel.owner.value)
    }

    @Test
    fun `loadListing failure sets error and clears loading`() = runTest {
        whenever(getListingDetailsUseCase("L2"))
            .thenReturn(Result.failure(Exception("Not found")))

        viewModel.loadListing("L2")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Not found", viewModel.error.value)
        assertNull(viewModel.listing.value)
        assertNull(viewModel.owner.value)
    }

    @Test
    fun `loadPaymentMethods populates default methods`() {
        viewModel.loadPaymentMethods()
        val methods = viewModel.paymentMethods.value
        assertEquals(2, methods.size)
        assertEquals("pm_1234", methods[0].id)
        assertEquals("Visa", methods[0].cardBrand)
        assertEquals("4242", methods[0].last4)
    }

    @Test
    fun `addPaymentMethod appends to existing list`() {
        viewModel.loadPaymentMethods()
        val newMethod = PaymentMethod("pm_new", "Amex", "0000")
        viewModel.addPaymentMethod(newMethod)
        val methods = viewModel.paymentMethods.value
        assertTrue(methods.contains(newMethod))
    }

    @Test
    fun `processPayment error when listing not loaded`() = runTest {
        whenever(createPaymentIntentUseCase(20.0))
            .thenReturn(Result.success("secret"))

        viewModel.processPayment("L", "pm", 20.0)
        advanceUntilIdle()

        val state = viewModel.paymentState.value
        assertTrue(state is PaymentViewModel.PaymentState.Error)
        assertEquals("Listing not found", state.message)
    }

    @Test
    fun `processPayment error when owner not loaded`() = runTest {
        val dummyListing = Listing("L3", "O3", "P3", "", "", 0L, 0L, 0.0, ListingStatus.ACTIVE, 0L)
        viewModel.apply {
            this::class.java.getDeclaredField("_listing").apply {
                isAccessible = true
                (get(viewModel) as MutableStateFlow<Listing?>).value = dummyListing
            }
        }
        whenever(createPaymentIntentUseCase(30.0))
            .thenReturn(Result.success("secret"))

        viewModel.processPayment("L3", "pm", 30.0)
        advanceUntilIdle()

        val state = viewModel.paymentState.value as PaymentViewModel.PaymentState.Error
        assertEquals("Owner not found", state.message)
    }

    @Test
    fun `processPayment success sets Success state with transactionId`() = runTest {
        val dummyListing = Listing("L4", "O4", "P4", "", "", 0L, 0L, 0.0, ListingStatus.ACTIVE, 0L)
        val dummyOwner = User("O4", "Name", "e", "p", null, true, 0f, 0)
        viewModel.apply {
            this::class.java.getDeclaredField("_listing").apply {
                isAccessible = true
                (get(viewModel) as MutableStateFlow<Listing?>).value = dummyListing
            }
            this::class.java.getDeclaredField("_owner").apply {
                isAccessible = true
                (get(viewModel) as MutableStateFlow<User?>).value = dummyOwner
            }
        }

        whenever(createPaymentIntentUseCase(40.0)).thenReturn(Result.success("secret-id"))
        whenever(processPaymentUseCase("secret-id", "L4", "O4", 40.0))
            .thenReturn(
                Result.success(
                    Transaction(
                        "txn",
                        "L4",
                        "",
                        "O4",
                        40.0,
                        TransactionStatus.COMPLETED,
                        0L
                    )
                )
            )

        viewModel.processPayment("L4", "pm", 40.0)
        advanceUntilIdle()

        val state = viewModel.paymentState.value as PaymentViewModel.PaymentState.Success
        assertEquals("txn", state.transactionId)
    }

    @Test
    fun `resetPaymentState resets to Idle`() {
        viewModel.resetPaymentState()
        assertTrue(viewModel.paymentState.value is PaymentViewModel.PaymentState.Idle)
    }
}
