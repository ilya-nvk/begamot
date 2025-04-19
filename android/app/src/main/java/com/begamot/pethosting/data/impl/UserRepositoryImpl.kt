package com.begamot.pethosting.data.impl

import android.net.Uri
import com.begamot.pethosting.PetHostingApplication
import com.begamot.pethosting.data.api.ApiService
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.data.models.requestresponse.LoginRequest
import com.begamot.pethosting.data.models.requestresponse.RegisterRequest
import com.begamot.pethosting.data.models.requestresponse.UpdateUserRequest
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import com.begamot.pethosting.data.models.requestresponse.VerifyRequest
import com.begamot.pethosting.data.repositories.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : UserRepository {
    
    override suspend fun getCurrentUser(): Result<User> {
        val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
        return getUserById(userId)
    }
    
    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUser())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun registerUser(email: String, password: String, user: User): Result<User> {
        return try {
            val request = RegisterRequest(
                email = email,
                password = password,
                fullName = user.fullName,
                phone = user.phone
            )
            val response = apiService.registerUser(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveTokens(
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken,
                    userId = authResponse.userId
                )
                Result.success(authResponse.user.toUser())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = apiService.loginUser(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveTokens(
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken,
                    userId = authResponse.userId
                )
                Result.success(authResponse.user.toUser())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logoutUser() {
        tokenManager.clearTokens()
    }
    
    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val request = UpdateUserRequest(
                fullName = user.fullName,
                phone = user.phone
            )
            val response = apiService.updateUser(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUser())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadUserProfileImage(imageUri: Uri): Result<String> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val context = PetHostingApplication.appContext
            val contentResolver = context.contentResolver
            
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("Failed to open image"))
            
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val requestFile = inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", fileName, requestFile)
            
            val response = apiService.uploadUserImage(userId, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.imageUrl)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to upload image"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyUser(idImageUri: Uri): Result<Boolean> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val context = PetHostingApplication.appContext
            val contentResolver = context.contentResolver
            
            val inputStream = contentResolver.openInputStream(idImageUri)
                ?: return Result.failure(Exception("Failed to open image"))
            
            val fileName = "id_${System.currentTimeMillis()}.jpg"
            val requestFile = inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("idImage", fileName, requestFile)
            
            val request = VerifyRequest(userId = userId)
            val response = apiService.verifyUser(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.isVerificationStarted)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to start verification"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper extension functions
    private fun UserResponse.toUser(): User {
        return User(
            id = id,
            fullName = fullName,
            email = email,
            phone = phone,
            profileImageUrl = profileImageUrl,
            isVerified = isVerified,
            rating = rating,
            reviewCount = reviewCount
        )
    }
}
