package com.begamot.pethosting.ui

import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.domain.usecases.CreatePetUseCase
import com.begamot.pethosting.domain.usecases.DeletePetUseCase
import com.begamot.pethosting.domain.usecases.GetPetByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserPetsUseCase
import com.begamot.pethosting.domain.usecases.UpdatePetUseCase
import com.begamot.pethosting.domain.usecases.UploadPetImageUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PetViewModelTest {

    private val getUserPetsUseCase: GetUserPetsUseCase = mock()
    private val getPetByIdUseCase: GetPetByIdUseCase = mock()
    private val createPetUseCase: CreatePetUseCase = mock()
    private val updatePetUseCase: UpdatePetUseCase = mock()
    private val deletePetUseCase: DeletePetUseCase = mock()
    private val uploadPetImageUseCase: UploadPetImageUseCase = mock()

    private lateinit var viewModel: PetViewModel
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = PetViewModel(
            getUserPetsUseCase,
            getPetByIdUseCase,
            createPetUseCase,
            updatePetUseCase,
            deletePetUseCase,
            uploadPetImageUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserPets success updates pets and clears loading`() = runTest {
        val dummyList = listOf(Pet("1", "o", "n", "t", "b", 1, "d", emptyList()))
        whenever(getUserPetsUseCase()).thenReturn(Result.success(dummyList))

        viewModel.loadUserPets()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals(dummyList, viewModel.pets.value)
        assertTrue(viewModel.actionState.value is PetViewModel.ActionState.Idle)
    }

    @Test
    fun `loadPetDetails success updates currentPet and clears loading`() = runTest {
        val pet = Pet("2", "o2", "n2", "t2", "b2", 2, "d2", emptyList())
        whenever(getPetByIdUseCase("2")).thenReturn(Result.success(pet))

        viewModel.loadPetDetails("2")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals(pet, viewModel.currentPet.value)
        assertTrue(viewModel.actionState.value is PetViewModel.ActionState.Idle)
    }

    @Test
    fun `createPet success triggers reload and sets Success state`() = runTest {
        val created = Pet("3", "o3", "n3", "t3", "b3", 3, "d3", emptyList())
        val reloadedList = listOf(created)
        whenever(createPetUseCase(created, null)).thenReturn(Result.success(created))
        whenever(getUserPetsUseCase()).thenReturn(Result.success(reloadedList))

        viewModel.createPet(created, null)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.actionState.value is PetViewModel.ActionState.Success)
        assertEquals(reloadedList, viewModel.pets.value)
    }

    @Test
    fun `updatePet success updates currentPet, reloads pets and sets Success state`() = runTest {
        val updated = Pet("4", "o4", "n4", "t4", "b4", 4, "d4", emptyList())
        val reloaded = listOf(updated)
        whenever(updatePetUseCase(updated)).thenReturn(Result.success(updated))
        whenever(getUserPetsUseCase()).thenReturn(Result.success(reloaded))

        viewModel.updatePet(updated)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.actionState.value is PetViewModel.ActionState.Success)
        assertEquals(updated, viewModel.currentPet.value)
        assertEquals(reloaded, viewModel.pets.value)
    }

    @Test
    fun `deletePet success clears currentPet, reloads pets and sets Success state`() = runTest {
        val remaining = emptyList<Pet>()
        whenever(deletePetUseCase("5")).thenReturn(Result.success(true))
        whenever(getUserPetsUseCase()).thenReturn(Result.success(remaining))

        viewModel.deletePet("5")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.actionState.value is PetViewModel.ActionState.Success)
        assertNull(viewModel.currentPet.value)
        assertEquals(remaining, viewModel.pets.value)
    }

    @Test
    fun `resetActionState resets to Idle`() {
        viewModel.resetActionState()
        assertTrue(viewModel.actionState.value is PetViewModel.ActionState.Idle)
    }
}
