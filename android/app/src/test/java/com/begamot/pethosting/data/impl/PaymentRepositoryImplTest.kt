package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Transaction
import com.begamot.pethosting.data.models.TransactionStatus
import com.begamot.pethosting.data.models.requestresponse.CreatePaymentIntentRequest
import com.begamot.pethosting.data.models.requestresponse.PaymentIntentResponse
import com.begamot.pethosting.data.models.requestresponse.ProcessPaymentRequest
import com.begamot.pethosting.data.models.requestresponse.TransactionResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentRepositoryImplTest {

    private val apiService: ApiService = mock()
    private lateinit var repository: PaymentRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = PaymentRepositoryImpl(apiService)
    }

    @Test
    fun `createPaymentIntent returns clientSecret on success`() = runBlocking {
        whenever(apiService.createPaymentIntent(any<CreatePaymentIntentRequest>()))
            .thenReturn(Response.success(PaymentIntentResponse("secret-xyz")))

        val result = repository.createPaymentIntent(123.0, "USD")
        assertTrue(result.isSuccess)
        assertEquals("secret-xyz", result.getOrThrow())
    }

    @Test
    fun `processPayment returns Transaction mapped from response`() = runBlocking {
        val resp = TransactionResponse(
            id = "tx1",
            listingId = "list1",
            payerId = "payer1",
            receiverId = "recv1",
            amount = 45.6,
            status = TransactionStatus.COMPLETED.name,
            createdAt = 1_600_000_000L
        )
        whenever(apiService.processPayment(any<ProcessPaymentRequest>()))
            .thenReturn(Response.success(resp))

        val input = Transaction(
            id = "ignore",
            listingId = "list1",
            payerId = "payer1",
            receiverId = "recv1",
            amount = 45.6,
            status = TransactionStatus.PENDING,
            createdAt = 0L
        )
        val result = repository.processPayment("pi_123", input)
        assertTrue(result.isSuccess)
        val out = result.getOrThrow()
        assertEquals("tx1", out.id)
        assertEquals("list1", out.listingId)
        assertEquals("payer1", out.payerId)
        assertEquals("recv1", out.receiverId)
        assertEquals(45.6, out.amount)
        assertEquals(TransactionStatus.COMPLETED, out.status)
        assertEquals(1_600_000_000L, out.createdAt)
    }

    @Test
    fun `refundPayment returns Transaction mapped from response`() = runBlocking {
        val resp = TransactionResponse(
            id = "tx2",
            listingId = "list2",
            payerId = "payer2",
            receiverId = "recv2",
            amount = 78.9,
            status = TransactionStatus.REFUNDED.name,
            createdAt = 1_600_000_100L
        )
        whenever(apiService.refundPayment(eq("tx2")))
            .thenReturn(Response.success(resp))

        val result = repository.refundPayment("tx2")
        assertTrue(result.isSuccess)
        val out = result.getOrThrow()
        assertEquals("tx2", out.id)
        assertEquals(TransactionStatus.REFUNDED, out.status)
        assertEquals(1_600_000_100L, out.createdAt)
    }
}
