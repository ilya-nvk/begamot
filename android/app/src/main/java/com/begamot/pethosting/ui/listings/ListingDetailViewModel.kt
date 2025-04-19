package com.begamot.pethosting.ui.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.ResponseModel
import com.begamot.pethosting.data.models.ResponseStatus
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.CreateResponseUseCase
import com.begamot.pethosting.domain.usecases.GetCurrentUserUseCase
import com.begamot.pethosting.domain.usecases.GetListingDetailsUseCase
import com.begamot.pethosting.domain.usecases.GetListingResponsesUseCase
import com.begamot.pethosting.domain.usecases.UpdateResponseStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val getListingDetailsUseCase: GetListingDetailsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getListingResponsesUseCase: GetListingResponsesUseCase,
    private val createResponseUseCase: CreateResponseUseCase,
    private val updateResponseStatusUseCase: UpdateResponseStatusUseCase
) : ViewModel() {

    private val _listing = MutableStateFlow<Listing?>(null)
    val listing: StateFlow<Listing?> = _listing.asStateFlow()

    private val _pet = MutableStateFlow<Pet?>(null)
    val pet: StateFlow<Pet?> = _pet.asStateFlow()

    private val _owner = MutableStateFlow<User?>(null)
    val owner: StateFlow<User?> = _owner.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _responses = MutableStateFlow<List<ResponseModel>>(emptyList())
    val responses: StateFlow<List<ResponseModel>> = _responses.asStateFlow()

    private val _responseState = MutableStateFlow<ResponseState>(ResponseState.NotSent)
    val responseState: StateFlow<ResponseState> = _responseState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadListing(listingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getListingDetailsUseCase(listingId)

            result.fold(
                onSuccess = {
                    _listing.value = it.listing
                    _pet.value = it.pet
                    _owner.value = it.owner
                    loadResponses(listingId)
                },
                onFailure = {
                    _error.value = it.message
                }
            )

            _isLoading.value = false
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()

            result.fold(
                onSuccess = { _currentUser.value = it },
                onFailure = { /* Silently fail */ }
            )
        }
    }

    private fun loadResponses(listingId: String) {
        viewModelScope.launch {
            val result = getListingResponsesUseCase(listingId)

            result.fold(
                onSuccess = { responses ->
                    _responses.value = responses

                    // Check if current user has a response
                    val currentUserId = _currentUser.value?.id
                    if (currentUserId != null) {
                        val userResponse = responses.find { it.responderId == currentUserId }

                        _responseState.value = when {
                            userResponse == null -> ResponseState.NotSent
                            userResponse.status == ResponseStatus.PENDING -> ResponseState.Pending
                            userResponse.status == ResponseStatus.ACCEPTED -> ResponseState.Accepted
                            else -> ResponseState.NotSent
                        }
                    }
                },
                onFailure = { /* Silently fail */ }
            )
        }
    }

    fun respondToListing(message: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val currentUserId = _currentUser.value?.id ?: return@launch
            val listingId = _listing.value?.id ?: return@launch

            val response = ResponseModel(
                listingId = listingId,
                responderId = currentUserId,
                message = message
            )

            val result = createResponseUseCase(response)

            result.fold(
                onSuccess = {
                    _responseState.value = ResponseState.Pending
                    _responses.value += it
                },
                onFailure = {
                    _error.value = it.message
                }
            )

            _isLoading.value = false
        }
    }

    fun updateResponseStatus(responseId: String, status: ResponseStatus) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = updateResponseStatusUseCase(responseId, status)

            result.fold(
                onSuccess = {
                    // Update in the list
                    val updatedResponses = _responses.value.map { resp ->
                        if (resp.id == responseId) it else resp
                    }
                    _responses.value = updatedResponses
                },
                onFailure = {
                    _error.value = it.message
                }
            )

            _isLoading.value = false
        }
    }

    sealed class ResponseState {
        data object NotSent : ResponseState()
        data object Pending : ResponseState()
        data object Accepted : ResponseState()
        data object Rejected : ResponseState()
    }
}
