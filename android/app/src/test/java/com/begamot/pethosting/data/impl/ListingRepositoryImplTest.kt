package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.ListingStatus
import com.begamot.pethosting.data.models.requestresponse.CreateListingRequest
import com.begamot.pethosting.data.models.requestresponse.ListingResponse
import com.begamot.pethosting.data.models.requestresponse.UpdateListingRequest
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

class ListingRepositoryImplTest {

    private val apiService: ApiService = mock()
    private lateinit var repository: ListingRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = ListingRepositoryImpl(apiService)
    }

    @Test
    fun `getAllListings returns mapped listings on success`() = runBlocking {
        val resp = ListingResponse(
            id = "1",
            ownerId = "owner",
            petId = "pet",
            title = "T",
            description = "D",
            startDate = 1000L,
            endDate = 2000L,
            price = 50.0,
            status = ListingStatus.ACTIVE.name,
            createdAt = 3000L
        )
        whenever(apiService.getListings(any<Map<String, String>>()))
            .thenReturn(Response.success(listOf(resp)))

        val result = repository.getAllListings(null)
        assertTrue(result.isSuccess)
        val listings = result.getOrThrow()
        assertEquals(1, listings.size)
        with(listings[0]) {
            assertEquals("1", id)
            assertEquals("owner", ownerId)
            assertEquals(50.0, price)
            assertEquals(ListingStatus.ACTIVE, status)
        }
    }

    @Test
    fun `getListingsByOwnerId maps CANCELLED status correctly`() = runBlocking {
        val resp = ListingResponse(
            id = "2",
            ownerId = "X",
            petId = "pet2",
            title = "TT",
            description = "DD",
            startDate = 10L,
            endDate = 20L,
            price = 75.0,
            status = ListingStatus.CANCELLED.name,
            createdAt = 30L
        )
        whenever(apiService.getListings(any()))
            .thenReturn(Response.success(listOf(resp)))

        val result = repository.getListingsByOwnerId("X")
        assertTrue(result.isSuccess)
        val listing = result.getOrThrow().first()
        assertEquals("2", listing.id)
        assertEquals(ListingStatus.CANCELLED, listing.status)
    }

    @Test
    fun `createListing returns new listing on success`() = runBlocking {
        val input = Listing(
            id = "orig",
            ownerId = "o",
            title = "t",
            description = "d",
            petId = "p",
            startDate = 1L,
            endDate = 2L,
            price = 100.0,
            status = ListingStatus.ACTIVE,
            createdAt = 0L
        )
        val resp = ListingResponse(
            id = "new",
            ownerId = "o",
            petId = "p",
            title = "t",
            description = "d",
            startDate = 1L,
            endDate = 2L,
            price = 100.0,
            status = ListingStatus.ACTIVE.name,
            createdAt = 0L
        )
        whenever(apiService.createListing(any<CreateListingRequest>()))
            .thenReturn(Response.success(resp))

        val result = repository.createListing(input)
        assertTrue(result.isSuccess)
        assertEquals("new", result.getOrThrow().id)
    }

    @Test
    fun `updateListing returns updated listing on success`() = runBlocking {
        val input = Listing(
            id = "upd",
            ownerId = "u",
            title = "tt",
            description = "dd",
            petId = "pp",
            startDate = 5L,
            endDate = 6L,
            price = 150.0,
            status = ListingStatus.BOOKED,
            createdAt = 0L
        )
        val resp = ListingResponse(
            id = "upd",
            ownerId = "u",
            petId = "pp",
            title = "tt",
            description = "dd",
            startDate = 5L,
            endDate = 6L,
            price = 150.0,
            status = ListingStatus.BOOKED.name,
            createdAt = 0L
        )
        whenever(apiService.updateListing(eq("upd"), any<UpdateListingRequest>()))
            .thenReturn(Response.success(resp))

        val result = repository.updateListing(input)
        assertTrue(result.isSuccess)
        assertEquals(ListingStatus.BOOKED, result.getOrThrow().status)
    }

    @Test
    fun `deleteListing returns true on success`() = runBlocking {
        whenever(apiService.deleteListing("del"))
            .thenReturn(Response.success(null))

        val result = repository.deleteListing("del")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }
}
