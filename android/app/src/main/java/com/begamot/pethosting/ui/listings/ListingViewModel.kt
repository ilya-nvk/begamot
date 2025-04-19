package com.begamot.pethosting.ui.listings

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.models.ListingDetail
import com.begamot.pethosting.domain.usecases.CreateListingUseCase
import com.begamot.pethosting.domain.usecases.CreatePetUseCase
import com.begamot.pethosting.domain.usecases.DeleteListingUseCase
import com.begamot.pethosting.domain.usecases.DeletePetUseCase
import com.begamot.pethosting.domain.usecases.GetAllListingsUseCase
import com.begamot.pethosting.domain.usecases.GetListingDetailsUseCase
import com.begamot.pethosting.domain.usecases.GetPetByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserListingsUseCase
import com.begamot.pethosting.domain.usecases.GetUserPetsUseCase
import com.begamot.pethosting.domain.usecases.UpdateListingUseCase
import com.begamot.pethosting.domain.usecases.UpdatePetUseCase
import com.begamot.pethosting.domain.usecases.UploadPetImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingViewModel @Inject constructor(
    private val getAllListingsUseCase: GetAllListingsUseCase,
    private val getUserListingsUseCase: GetUserListingsUseCase,
    private val getListingDetailsUseCase: GetListingDetailsUseCase,
    private val createListingUseCase: CreateListingUseCase,
    private val updateListingUseCase: UpdateListingUseCase,
    private val deleteListingUseCase: DeleteListingUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase
) : ViewModel() {
    
    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()
    
    private val _userListings = MutableStateFlow<List<Listing>>(emptyList())
    val userListings: StateFlow<List<Listing>> = _userListings.asStateFlow()
    
    private val _listingDetail = MutableStateFlow<ListingDetail?>(null)
    val listingDetail: StateFlow<ListingDetail?> = _listingDetail.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()
    
    // Cache for users and pets
    private val _users = mutableMapOf<String, MutableStateFlow<User?>>()
    private val _pets = mutableMapOf<String, MutableStateFlow<Pet?>>()

    fun loadListings(filters: Map<String, Any>? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = getAllListingsUseCase(filters)
            
            result.fold(
                onSuccess = { _listings.value = it },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "Failed to load listings") }
            )
            
            _isLoading.value = false
        }
    }
    
    private fun loadUserListings() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = getUserListingsUseCase()
            
            result.fold(
                onSuccess = { _userListings.value = it },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "Failed to load your listings") }
            )
            
            _isLoading.value = false
        }
    }
    
    private fun loadListingDetails(listingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = getListingDetailsUseCase(listingId)
            
            result.fold(
                onSuccess = { _listingDetail.value = it },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "Failed to load listing details") }
            )
            
            _isLoading.value = false
        }
    }
    
    fun createListing(listing: Listing) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading
            
            val result = createListingUseCase(listing)
            
            result.fold(
                onSuccess = {
                    _actionState.value = ActionState.Success
                    loadUserListings() // Reload user listings
                },
                onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to create listing")
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun updateListing(listing: Listing) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading
            
            val result = updateListingUseCase(listing)
            
            result.fold(
                onSuccess = {
                    _actionState.value = ActionState.Success
                    // Update in both lists if present
                    val updatedUserListings = _userListings.value.map { if (it.id == listing.id) listing else it }
                    _userListings.value = updatedUserListings

            val updatedListings = _listings.value.map { if (it.id == listing.id) listing else it }
            _listings.value = updatedListings

            // Reload detail if viewing this listing
            if (_listingDetail.value?.listing?.id == listing.id) {
                loadListingDetails(listing.id)
            }
                },
            onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to update listing")
                }
            )

            _isLoading.value = false
        }
    }

    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading

            val result = deleteListingUseCase(listingId)

            result.fold(
                    onSuccess = {
                            _actionState.value = ActionState.Success

                            // Remove from both lists if present
                            _userListings.value = _userListings.value.filter { it.id != listingId }
                            _listings.value = _listings.value.filter { it.id != listingId }

                    // Clear detail if viewing this listing
            if (_listingDetail.value?.listing?.id == listingId) {
                _listingDetail.value = null
            }
                },
            onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to delete listing")
                }
            )

            _isLoading.value = false
        }
    }

    fun getUser(userId: String): StateFlow<User?> {
        return _users.getOrPut(userId) {
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

    fun getPet(petId: String): StateFlow<Pet?> {
        return _pets.getOrPut(petId) {
            val petFlow = MutableStateFlow<Pet?>(null)

            viewModelScope.launch {
                val result = getPetByIdUseCase(petId)
                result.fold(
                        onSuccess = { petFlow.value = it },
                        onFailure = { /* Silently fail */ }
                )
            }

            petFlow
        }
    }

    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }

    sealed class ActionState {
        data object Idle : ActionState()
        data object Loading : ActionState()
        data object Success : ActionState()
        data class Error(val message: String) : ActionState()
    }
}
