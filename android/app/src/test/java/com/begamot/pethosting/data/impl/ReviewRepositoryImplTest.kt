package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Review
import com.begamot.pethosting.data.models.requestresponse.CreateReviewRequest
import com.begamot.pethosting.data.models.requestresponse.ReviewResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReviewRepositoryImplTest {

    private val apiService: ApiService = mock()
    private lateinit var repository: ReviewRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = ReviewRepositoryImpl(apiService)
    }

    @Test
    fun `getReviewsByReceiverId returns mapped list`() = runBlocking {
        val resp1 = ReviewResponse(
            id = "r1",
            listingId = "l1",
            reviewerId = "rev1",
            receiverId = "rcv",
            rating = 4f,
            comment = "good",
            createdAt = 100L
        )
        val resp2 = ReviewResponse(
            id = "r2",
            listingId = "l2",
            reviewerId = "rev2",
            receiverId = "rcv",
            rating = 5f,
            comment = "great",
            createdAt = 200L
        )
        whenever(apiService.getUserReviews("rcv"))
            .thenReturn(Response.success(listOf(resp1, resp2)))

        val result = repository.getReviewsByReceiverId("rcv")
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
        list.zip(listOf(resp1, resp2)).forEach { (model, resp) ->
            assertEquals(resp.id, model.id)
            assertEquals(resp.listingId, model.listingId)
            assertEquals(resp.reviewerId, model.reviewerId)
            assertEquals(resp.receiverId, model.receiverId)
            assertEquals(resp.rating, model.rating)
            assertEquals(resp.comment, model.comment)
            assertEquals(resp.createdAt, model.createdAt)
        }
    }

    @Test
    fun `createReview sends correct request and returns mapped review`(): Unit = runBlocking {
        val input = Review(
            id = "ignored",
            listingId = "l3",
            reviewerId = "rev3",
            receiverId = "rcv3",
            rating = 3f,
            comment = "okay",
            createdAt = 0L
        )
        val resp = ReviewResponse(
            id = "r3",
            listingId = "l3",
            reviewerId = "rev3",
            receiverId = "rcv3",
            rating = 3f,
            comment = "okay",
            createdAt = 300L
        )
        whenever(apiService.createReview(any<CreateReviewRequest>()))
            .thenReturn(Response.success(resp))

        val result = repository.createReview(input)
        assertTrue(result.isSuccess)
        val out = result.getOrThrow()
        assertEquals(resp.id, out.id)
        assertEquals(resp.listingId, out.listingId)
        assertEquals(resp.reviewerId, out.reviewerId)
        assertEquals(resp.receiverId, out.receiverId)
        assertEquals(resp.rating, out.rating)
        assertEquals(resp.comment, out.comment)
        assertEquals(resp.createdAt, out.createdAt)

        argumentCaptor<CreateReviewRequest>().apply {
            verify(apiService).createReview(capture())
            val req = firstValue
            assertEquals(input.listingId, req.listingId)
            assertEquals(input.receiverId, req.receiverId)
            assertEquals(input.rating, req.rating)
            assertEquals(input.comment, req.comment)
        }
    }
}
