package com.begamot.pethosting.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.models.Listing
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.CreatePaymentIntentUseCase
import com.begamot.pethosting.domain.usecases.GetListingDetailsUseCase
import com.begamot.pethosting.domain.usecases.ProcessPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getListingDetailsUseCase: GetListingDetailsUseCase,
    private val createPaymentIntentUseCase: CreatePaymentIntentUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase
) : ViewModel() {
    
    private val _listing = MutableStateFlow<Listing?>(null)
    val listing: StateFlow<Listing?> = _listing.asStateFlow()
    
    private val _owner = MutableStateFlow<User?>(null)
    val owner: StateFlow<User?> = _owner.asStateFlow()
    
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadListing(listingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = getListingDetailsUseCase(listingId)
            
            result.fold(
                onSuccess = {
                    _listing.value = it.listing
                    _owner.value = it.owner
                },
                onFailure = { _error.value = it.message }
            )
            
            _isLoading.value = false
        }
    }
    
    fun loadPaymentMethods() {
        // In a real app, this would call the API to get saved payment methods
        // For this example, we'll use dummy data
        _paymentMethods.value = listOf(
            PaymentMethod("pm_1234", "Visa", "4242"),
            PaymentMethod("pm_5678", "Mastercard", "5555")
        )
    }
    
    fun addPaymentMethod(paymentMethod: PaymentMethod) {
        // In a real app, this would call the API to add a payment method
        // For this example, we'll just add it to the list
        _paymentMethods.value += paymentMethod
    }
    
    fun processPayment(listingId: String, paymentMethodId: String, amount: Double) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading

            val intentResult = createPaymentIntentUseCase(amount)
            
            intentResult.fold(
                onSuccess = { clientSecret ->
                    val listing = _listing.value ?: run {
                        _paymentState.value = PaymentState.Error("Listing not found")
                        return@launch
                    }
                    
                    val owner = _owner.value ?: run {
                        _paymentState.value = PaymentState.Error("Owner not found")
                        return@launch
                    }
                    
                    val paymentResult = processPaymentUseCase(
                        paymentIntentId = clientSecret,
                        listingId = listingId,
                        receiverId = owner.id,
                        amount = amount
                    )
                    
                    paymentResult.fold(
                        onSuccess = { transaction ->
                            _paymentState.value = PaymentState.Success(transaction.id)
                        },
                        onFailure = { error ->
                            _paymentState.value = PaymentState.Error(error.message ?: "Payment processing failed")
                        }
                    )
                },
                onFailure = { error ->
                    _paymentState.value = PaymentState.Error(error.message ?: "Failed to create payment intent")
                }
            )
        }
    }
    
    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
    
    sealed class PaymentState {
        data object Idle : PaymentState()
        data object Loading : PaymentState()
        data class Success(val transactionId: String) : PaymentState()
        data class Error(val message: String) : PaymentState()
    }
}
