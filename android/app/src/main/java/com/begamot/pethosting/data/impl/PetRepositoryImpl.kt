package com.begamot.pethosting.data.impl

import android.net.Uri
import com.begamot.pethosting.PetHostingApplication
import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.requestresponse.CreatePetRequest
import com.begamot.pethosting.data.models.requestresponse.PetResponse
import com.begamot.pethosting.data.models.requestresponse.UpdatePetRequest
import com.begamot.pethosting.data.repositories.PetRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PetRepository {
    
    override suspend fun getPetsByOwnerId(ownerId: String): Result<List<Pet>> {
        return try {
            val response = apiService.getUserPets()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toPet() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get pets"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPetById(petId: String): Result<Pet> {
        return try {
            val response = apiService.getPetById(petId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toPet())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get pet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPet(pet: Pet): Result<Pet> {
        return try {
            val request = CreatePetRequest(
                name = pet.name,
                type = pet.type,
                breed = pet.breed,
                age = pet.age,
                description = pet.description
            )
            val response = apiService.createPet(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toPet())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create pet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePet(pet: Pet): Result<Pet> {
        return try {
            val request = UpdatePetRequest(
                name = pet.name,
                type = pet.type,
                breed = pet.breed,
                age = pet.age,
                description = pet.description
            )
            val response = apiService.updatePet(pet.id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toPet())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update pet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deletePet(petId: String): Result<Boolean> {
        return try {
            val response = apiService.deletePet(petId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete pet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadPetImage(petId: String, imageUri: Uri): Result<String> {
        return try {
            val context = PetHostingApplication.appContext
            val contentResolver = context.contentResolver
            
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("Failed to open image"))
            
            val fileName = "pet_${System.currentTimeMillis()}.jpg"
            val requestFile = inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", fileName, requestFile)
            
            val response = apiService.uploadPetImage(petId, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.imageUrl)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to upload image"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper extension functions
    private fun PetResponse.toPet(): Pet {
        return Pet(
            id = id,
            ownerId = ownerId,
            name = name,
            type = type,
            breed = breed,
            age = age,
            description = description,
            imageUrls = imageUrls
        )
    }
}
