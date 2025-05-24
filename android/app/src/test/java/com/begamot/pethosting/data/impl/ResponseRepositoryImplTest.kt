package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.ResponseModel
import com.begamot.pethosting.data.models.ResponseStatus
import com.begamot.pethosting.data.models.requestresponse.CreateResponseRequest
import com.begamot.pethosting.data.models.requestresponse.ResponseResponse
import com.begamot.pethosting.data.models.requestresponse.UpdateResponseStatusRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResponseRepositoryImplTest {

    private val apiService: ApiService = mock()
    private lateinit var repository: ResponseRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = ResponseRepositoryImpl(apiService)
    }

    @Test
    fun `getResponsesByListingId returns mapped models on success`() = runBlocking {
        val resp = ResponseResponse(
            id = "R1",
            listingId = "L1",
            responderId = "U1",
            message = "hello",
            status = ResponseStatus.PENDING.name,
            createdAt = 100L
        )
        whenever(apiService.getListingResponses("L1"))
            .thenReturn(Response.success(listOf(resp)))

        val result = repository.getResponsesByListingId("L1")
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(1, list.size)
        with(list[0]) {
            assertEquals("L1_R1", id)
            assertEquals("L1", listingId)
            assertEquals("U1", responderId)
            assertEquals("hello", message)
            assertEquals(ResponseStatus.PENDING, status)
            assertEquals(100L, createdAt)
        }
    }

    @Test
    fun `getResponsesByResponderId filters responses correctly`() = runBlocking {
        val resp1 = ResponseResponse("R1", "L1", "U1", "msg1", ResponseStatus.PENDING.name, 1L)
        val resp2 = ResponseResponse("R2", "L2", "U2", "msg2", ResponseStatus.PENDING.name, 2L)
        whenever(apiService.getListingResponses("all"))
            .thenReturn(Response.success(listOf(resp1, resp2)))

        val result = repository.getResponsesByResponderId("U1")
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(1, list.size)
        assertEquals("U1", list[0].responderId)
        assertEquals("L1_R1", list[0].id)
    }

    @Test
    fun `createResponse sends correct request and returns mapped model`(): Unit = runBlocking {
        val input = ResponseModel(
            id = "ignored",
            listingId = "L2",
            responderId = "U2",
            message = "hi",
            status = ResponseStatus.PENDING,
            createdAt = 0L
        )
        val resp = ResponseResponse("R2", "L2", "U2", "hi", ResponseStatus.PENDING.name, 50L)
        whenever(apiService.createResponse(eq("L2"), any<CreateResponseRequest>()))
            .thenReturn(Response.success(resp))

        val result = repository.createResponse(input)
        assertTrue(result.isSuccess)
        val out = result.getOrThrow()
        assertEquals("L2_R2", out.id)
        assertEquals("hi", out.message)
        assertEquals(50L, out.createdAt)

        argumentCaptor<CreateResponseRequest>().apply {
            verify(apiService).createResponse(eq("L2"), capture())
            assertEquals("hi", firstValue.message)
        }
    }

    @Test
    fun `updateResponseStatus sends correct request and returns mapped model`(): Unit =
        runBlocking {
            val listingId = "L3"
            val responseId = "R3"
            val compositeId = "${listingId}_$responseId"
            val resp = ResponseResponse(
                id = "R3",
                listingId = "L3",
                responderId = "U3",
                message = "ok",
                status = ResponseStatus.PENDING.name,
                createdAt = 75L
            )
            whenever(
                apiService.updateResponseStatus(
                    eq(listingId),
                    eq(responseId),
                    any<UpdateResponseStatusRequest>()
                )
            ).thenReturn(Response.success(resp))

            val result = repository.updateResponseStatus(compositeId, ResponseStatus.PENDING)
            assertTrue(result.isSuccess)
            val out = result.getOrThrow()
            assertEquals(compositeId, out.id)
            assertEquals(ResponseStatus.PENDING, out.status)

            argumentCaptor<UpdateResponseStatusRequest>().apply {
                verify(apiService).updateResponseStatus(eq(listingId), eq(responseId), capture())
                assertEquals(ResponseStatus.PENDING.name, firstValue.status)
            }
        }
}
