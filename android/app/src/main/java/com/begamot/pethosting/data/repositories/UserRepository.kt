package com.begamot.pethosting.data.repositories

import android.net.Uri
import com.begamot.pethosting.data.models.User

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun registerUser(email: String, password: String, user: User): Result<User>
    suspend fun loginUser(email: String, password: String): Result<User>
    suspend fun logoutUser()
    suspend fun updateUser(user: User): Result<User>
    suspend fun uploadUserProfileImage(imageUri: Uri): Result<String>
    suspend fun verifyUser(idImageUri: Uri): Result<Boolean>
}
