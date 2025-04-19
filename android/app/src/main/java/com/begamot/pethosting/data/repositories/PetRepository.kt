package com.begamot.pethosting.data.repositories

import android.net.Uri
import com.begamot.pethosting.data.models.Pet

interface PetRepository {
    suspend fun getPetsByOwnerId(ownerId: String): Result<List<Pet>>
    suspend fun getPetById(petId: String): Result<Pet>
    suspend fun createPet(pet: Pet): Result<Pet>
    suspend fun updatePet(pet: Pet): Result<Pet>
    suspend fun deletePet(petId: String): Result<Boolean>
    suspend fun uploadPetImage(petId: String, imageUri: Uri): Result<String>
}
