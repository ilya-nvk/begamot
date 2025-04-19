package com.begamot.pethosting.ui.messages

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.Pet
import com.begamot.pethosting.data.models.ResponseStatus
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.CreateListingUseCase
import com.begamot.pethosting.domain.usecases.CreatePetUseCase
import com.begamot.pethosting.domain.usecases.CreateResponseUseCase
import com.begamot.pethosting.domain.usecases.DeleteListingUseCase
import com.begamot.pethosting.domain.usecases.DeletePetUseCase
import com.begamot.pethosting.domain.usecases.GetAllListingsUseCase
import com.begamot.pethosting.domain.usecases.GetCurrentUserUseCase
import com.begamot.pethosting.domain.usecases.GetListingDetailsUseCase
import com.begamot.pethosting.domain.usecases.GetListingResponsesUseCase
import com.begamot.pethosting.domain.usecases.GetPetByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserByIdUseCase
import com.begamot.pethosting.domain.usecases.GetUserConversationsUseCase
import com.begamot.pethosting.domain.usecases.GetUserListingsUseCase
import com.begamot.pethosting.domain.usecases.GetUserPetsUseCase
import com.begamot.pethosting.domain.usecases.UpdateListingUseCase
import com.begamot.pethosting.domain.usecases.UpdatePetUseCase
import com.begamot.pethosting.domain.usecases.UpdateResponseStatusUseCase
import com.begamot.pethosting.domain.usecases.UploadPetImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val getUserConversationsUseCase: GetUserConversationsUseCase
) : ViewModel() {
    
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = getUserConversationsUseCase()
            
            result.fold(
                onSuccess = { _conversations.value = it },
                onFailure = { _error.value = it.message }
            )
            
            _isLoading.value = false
        }
    }
}
