package com.begamot.pethosting.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.Review
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.GetCurrentUserUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserListingsUseCase
import com.begamot.pethosting.domain.usecases.GetUserPetsUseCase
import com.begamot.pethosting.domain.usecases.GetUserReviewsUseCase
import com.begamot.pethosting.domain.usecases.LoginUseCase
import com.begamot.pethosting.domain.usecases.LogoutUseCase
import com.begamot.pethosting.domain.usecases.RegisterUseCase
import com.begamot.pethosting.domain.usecases.UpdateUserUseCase
import com.begamot.pethosting.domain.usecases.UploadUserProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val uploadUserProfileImageUseCase: UploadUserProfileImageUseCase,
    private val getUserPetsUseCase: GetUserPetsUseCase,
    private val getUserListingsUseCase: GetUserListingsUseCase,
    private val getUserReviewsUseCase: GetUserReviewsUseCase
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()
    
    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()
    
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // For getting other users' profiles
    private val _otherUsers = mutableMapOf<String, MutableStateFlow<User?>>()
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = getCurrentUserUseCase()
            
            result.fold(
                onSuccess = {
                    _user.value = it
                },
                onFailure = {
                    _error.value = it.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun loadUserPets() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = getUserPetsUseCase()
            
            result.fold(
                onSuccess = {
                    _pets.value = it
                },
                onFailure = {
                    _error.value = it.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun loadUserListings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = getUserListingsUseCase()
            
            result.fold(
                onSuccess = {
                    _listings.value = it
                },
                onFailure = {
                    _error.value = it.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun loadUserReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val user = _user.value ?: return@launch
            
            val result = getUserReviewsUseCase(user.id)
            
            result.fold(
                onSuccess = {
                    _reviews.value = it
                },
                onFailure = {
                    _error.value = it.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = updateUserUseCase(updatedUser)
            
            result.fold(
                onSuccess = {
                    _user.value = it
                },
                onFailure = {
                    _error.value = it.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = uploadUserProfileImageUseCase(imageUri)
            
            result.fold(
                onSuccess = {
                    // Reload user to get updated profile image
                    loadUserProfile()
                },
                onFailure = {
                    _error.value = it.message
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun getUserById(userId: String): StateFlow<User?> {
        return _otherUsers.getOrPut(userId) {
            val userFlow = MutableStateFlow<User?>(null)
            
            viewModelScope.launch {
                val result = getUserByIdUseCase(userId)
                result.fold(
                    onSuccess = { userFlow.value = it },
                    onFailure = { /* Silently fail */ }
                )
            }
            
            userFlow
        }
    }
}
