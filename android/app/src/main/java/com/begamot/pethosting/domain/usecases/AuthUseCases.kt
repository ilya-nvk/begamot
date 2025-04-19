package com.begamot.pethosting.domain.usecases

import android.net.Uri
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.data.repositories.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return userRepository.loginUser(email, password)
    }
}

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, user: User, profileImage: Uri?): Result<User> {
        // First register the user
        val registerResult = userRepository.registerUser(email, password, user)
        
        // If registration successful and profile image is provided, upload it
        if (registerResult.isSuccess && profileImage != null) {
            val uploadResult = userRepository.uploadUserProfileImage(profileImage)
            if (uploadResult.isSuccess) {
                // Return updated user with profile image
                return userRepository.getCurrentUser()
            }
        }
        
        return registerResult
    }
}

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.logoutUser()
    }
}

class VerifyUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(idImageUri: Uri): Result<Boolean> {
        return userRepository.verifyUser(idImageUri)
    }
}
