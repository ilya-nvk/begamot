package com.begamot.pethosting.domain.usecases

import android.net.Uri
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.repositories.PetRepository
import javax.inject.Inject

class GetUserPetsUseCase @Inject constructor(
    private val petRepository: PetRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<List<Pet>> {
        val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return petRepository.getPetsByOwnerId(userId)
    }
}

class GetPetByIdUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String): Result<Pet> {
        return petRepository.getPetById(petId)
    }
}

class CreatePetUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet, imageUri: Uri?): Result<Pet> {
        val createResult = petRepository.createPet(pet)

        if (createResult.isSuccess && imageUri != null) {
            val newPet = createResult.getOrNull()!!
            val uploadResult = petRepository.uploadPetImage(newPet.id, imageUri)

            if (uploadResult.isSuccess) {
                // Fetch updated pet with image
                return petRepository.getPetById(newPet.id)
            }
        }

        return createResult
    }
}

class UpdatePetUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet): Result<Pet> {
        return petRepository.updatePet(pet)
    }
}

class DeletePetUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String): Result<Boolean> {
        return petRepository.deletePet(petId)
    }
}

class UploadPetImageUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String, imageUri: Uri): Result<String> {
        return petRepository.uploadPetImage(petId, imageUri)
    }
}
