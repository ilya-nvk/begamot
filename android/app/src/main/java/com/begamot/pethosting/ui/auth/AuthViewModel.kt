package com.begamot.pethosting.ui.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.models.User
import com.begamot.pethosting.domain.usecases.LoginUseCase
import com.begamot.pethosting.domain.usecases.LogoutUseCase
import com.begamot.pethosting.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    tokenManager: TokenManager
) : ViewModel() {
    
    private val _loginState = MutableStateFlow(AuthState())
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow(AuthState())
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()
    
    private val _isUserLoggedIn = MutableStateFlow(tokenManager.isLoggedIn())
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()
    
    init {
        // Check login status on init
        _isUserLoggedIn.value = tokenManager.isLoggedIn()
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState(isLoading = true)
            
            val result = loginUseCase(email, password)
            
            result.fold(
                onSuccess = {
                    _loginState.value = AuthState(isSuccess = true)
                    _isUserLoggedIn.value = true
                },
                onFailure = {
                    _loginState.value = AuthState(error = it.message)
                }
            )
        }
    }
    
    fun register(email: String, password: String, user: User, profileImage: Uri? = null) {
        viewModelScope.launch {
            _registerState.value = AuthState(isLoading = true)
            
            val result = registerUseCase(email, password, user, profileImage)
            
            result.fold(
                onSuccess = {
                    _registerState.value = AuthState(isSuccess = true)
                },
                onFailure = {
                    _registerState.value = AuthState(error = it.message)
                }
            )
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _isUserLoggedIn.value = false
        }
    }

    fun resetLoginState() {
        _loginState.value = AuthState()
    }
    
    fun resetRegisterState() {
        _registerState.value = AuthState()
    }
}
