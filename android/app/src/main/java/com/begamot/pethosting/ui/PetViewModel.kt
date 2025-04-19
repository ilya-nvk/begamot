package com.begamot.pethosting.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.domain.usecases.CreatePetUseCase
import com.begamot.pethosting.domain.usecases.DeletePetUseCase
import com.begamot.pethosting.domain.usecases.GetPetByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserPetsUseCase
import com.begamot.pethosting.domain.usecases.UpdatePetUseCase
import com.begamot.pethosting.domain.usecases.UploadPetImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val getUserPetsUseCase: GetUserPetsUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val createPetUseCase: CreatePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val uploadPetImageUseCase: UploadPetImageUseCase
) : ViewModel() {
    
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()
    
    private val _currentPet = MutableStateFlow<Pet?>(null)
    val currentPet: StateFlow<Pet?> = _currentPet.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()
    
    fun loadUserPets() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = getUserPetsUseCase()
            
            result.fold(
                onSuccess = { _pets.value = it },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "Failed to load pets") }
            )
            
            _isLoading.value = false
        }
    }
    
    fun loadPetDetails(petId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = getPetByIdUseCase(petId)
            
            result.fold(
                onSuccess = { _currentPet.value = it },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "Failed to load pet details") }
            )
            
            _isLoading.value = false
        }
    }
    
    fun createPet(pet: Pet, imageUri: Uri? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading
            
            val result = createPetUseCase(pet, imageUri)
            
            result.fold(
                onSuccess = {
                    _actionState.value = ActionState.Success
                    loadUserPets() // Reload the list
                },
                onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to create pet")
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading
            
            val result = updatePetUseCase(pet)
            
            result.fold(
                onSuccess = {
                    _actionState.value = ActionState.Success
                    _currentPet.value = it
                    loadUserPets() // Reload the list
                },
                onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to update pet")
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun deletePet(petId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading
            
            val result = deletePetUseCase(petId)
            
            result.fold(
                onSuccess = {
                    _actionState.value = ActionState.Success
                    _currentPet.value = null
                    loadUserPets() // Reload the list
                },
                onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to delete pet")
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun uploadPetImage(petId: String, imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _actionState.value = ActionState.Loading
            
            val result = uploadPetImageUseCase(petId, imageUri)
            
            result.fold(
                onSuccess = {
                    _actionState.value = ActionState.Success
                    loadPetDetails(petId) // Reload pet to get updated images
                },
                onFailure = {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to upload image")
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }
    
    sealed class ActionState {
        object Idle : ActionState()
        object Loading : ActionState()
        object Success : ActionState()
        data class Error(val message: String) : ActionState()
    }
}
