package com.begamot.pethosting.data.impl

import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.requestresponse.CreatePetRequest
import com.begamot.pethosting.data.models.requestresponse.PetResponse
import com.begamot.pethosting.data.models.requestresponse.UpdatePetRequest
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

class PetRepositoryImplTest {

    private val apiService: ApiService = mock()
    private lateinit var repository: PetRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = PetRepositoryImpl(apiService)
    }

    @Test
    fun `getPetsByOwnerId returns mapped list`() = runBlocking {
        val resp = PetResponse(
            id = "p1",
            ownerId = "o1",
            name = "Buddy",
            type = "dog",
            breed = "beagle",
            age = 4,
            description = "friendly",
            imageUrls = listOf("url1", "url2")
        )
        whenever(apiService.getUserPets())
            .thenReturn(Response.success(listOf(resp)))

        val result = repository.getPetsByOwnerId("ignoredOwner")
        assertTrue(result.isSuccess)
        val pets = result.getOrThrow()
        assertEquals(1, pets.size)
        with(pets[0]) {
            assertEquals("p1", id)
            assertEquals("o1", ownerId)
            assertEquals("Buddy", name)
            assertEquals("dog", type)
            assertEquals("beagle", breed)
            assertEquals(4, age)
            assertEquals("friendly", description)
            assertEquals(listOf("url1", "url2"), imageUrls)
        }
    }

    @Test
    fun `getPetById returns mapped Pet`() = runBlocking {
        val resp = PetResponse(
            id = "p2",
            ownerId = "o2",
            name = "Milo",
            type = "cat",
            breed = "siamese",
            age = 2,
            description = "quiet",
            imageUrls = emptyList()
        )
        whenever(apiService.getPetById("p2"))
            .thenReturn(Response.success(resp))

        val result = repository.getPetById("p2")
        assertTrue(result.isSuccess)
        val pet = result.getOrThrow()
        assertEquals("p2", pet.id)
        assertEquals("o2", pet.ownerId)
        assertEquals("Milo", pet.name)
        assertEquals("cat", pet.type)
        assertEquals("siamese", pet.breed)
        assertEquals(2, pet.age)
        assertEquals("quiet", pet.description)
    }

    @Test
    fun `createPet sends correct request and returns mapped Pet`(): Unit = runBlocking {
        val input = Pet(
            id = "x", ownerId = "o", name = "Fido",
            type = "dog", breed = "mastiff", age = 5,
            description = "big", imageUrls = emptyList()
        )
        val resp = PetResponse(
            id = "p3",
            ownerId = "o",
            name = "Fido",
            type = "dog",
            breed = "mastiff",
            age = 5,
            description = "big",
            imageUrls = emptyList()
        )
        whenever(apiService.createPet(any<CreatePetRequest>()))
            .thenReturn(Response.success(resp))

        val result = repository.createPet(input)
        assertTrue(result.isSuccess)
        assertEquals("p3", result.getOrThrow().id)

        argumentCaptor<CreatePetRequest>().apply {
            verify(apiService).createPet(capture())
            val req = firstValue
            assertEquals("Fido", req.name)
            assertEquals("dog", req.type)
            assertEquals("mastiff", req.breed)
            assertEquals(5, req.age)
            assertEquals("big", req.description)
        }
    }

    @Test
    fun `updatePet sends correct request and returns mapped Pet`(): Unit = runBlocking {
        val input = Pet(
            id = "p4", ownerId = "o4", name = "Bella",
            type = "cat", breed = "persian", age = 3,
            description = "fluffy", imageUrls = emptyList()
        )
        val resp = PetResponse(
            id = "p4",
            ownerId = "o4",
            name = "Bella",
            type = "cat",
            breed = "persian",
            age = 3,
            description = "fluffy",
            imageUrls = emptyList()
        )
        whenever(apiService.updatePet(eq("p4"), any<UpdatePetRequest>()))
            .thenReturn(Response.success(resp))

        val result = repository.updatePet(input)
        assertTrue(result.isSuccess)
        assertEquals("Bella", result.getOrThrow().name)

        argumentCaptor<UpdatePetRequest>().apply {
            verify(apiService).updatePet(eq("p4"), capture())
            val req = firstValue
            assertEquals("Bella", req.name)
            assertEquals("cat", req.type)
            assertEquals("persian", req.breed)
            assertEquals(3, req.age)
            assertEquals("fluffy", req.description)
        }
    }

    @Test
    fun `deletePet returns true on success`() = runBlocking {
        whenever(apiService.deletePet("p5"))
            .thenReturn(Response.success(null))

        val result = repository.deletePet("p5")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }
}
