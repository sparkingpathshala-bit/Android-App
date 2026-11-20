package com.sparkingpathshala.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sparkingpathshala.app.data.local.UserEntity
import com.sparkingpathshala.app.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
    object PendingApproval : AuthUiState
}

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isUserLoading = MutableStateFlow(true)
    val isUserLoading = _isUserLoading.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = repository.getUser()
        .onEach { _isUserLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            // Safety timeout: ensure loading state never hangs indefinitely on cold start or offline mode
            kotlinx.coroutines.delay(1000L)
            _isUserLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(cleanEmail, cleanPassword)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Login failed. Check your connection.")
                }
        }
    }

    fun signup(name: String, email: String, classSelection: String, mobile: String, password: String) {
        if (name.isBlank() || email.isBlank() || classSelection.isBlank() || mobile.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!emailRegex.matches(email.trim())) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address")
            return
        }
        val mobileRegex = "^[0-9]{10}$".toRegex()
        if (!mobileRegex.matches(mobile.trim())) {
            _uiState.value = AuthUiState.Error("Mobile number must be exactly 10 digits")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters long")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signup(name, email.trim(), classSelection, mobile.trim(), password)
                .onSuccess { _ ->
                    _uiState.value = AuthUiState.PendingApproval
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Registration failed.")
                }
        }
    }

    fun loginAsGuest(context: android.content.Context) {
        val appContext = context.applicationContext ?: context
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val guestId = "guest_${java.util.UUID.randomUUID().toString().take(8)}"
            val manufacturer = android.os.Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
            val model = android.os.Build.MODEL
            val deviceName = if (model.startsWith(manufacturer, ignoreCase = true)) model else "$manufacturer $model"
            val deviceModel = android.os.Build.DEVICE ?: "Android"
            
            // Resolve verified real public IP and location (No fake/mock data)
            val telemetry = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                com.sparkingpathshala.app.security.SecurityUtils.fetchRealNetworkTelemetry(appContext)
            }
            
            repository.loginAsGuest(
                guestId = guestId,
                deviceName = deviceName,
                deviceModel = deviceModel,
                location = telemetry.location,
                ipAddress = telemetry.ipAddress
            ).onSuccess { guestUser ->
                _uiState.value = AuthUiState.Success(guestUser)
            }.onFailure { err ->
                _uiState.value = AuthUiState.Error(err.localizedMessage ?: "Guest login failed.")
            }
        }
    }

    fun resetAuthState() {
        _uiState.value = AuthUiState.Idle
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error || _uiState.value is AuthUiState.PendingApproval) {
            _uiState.value = AuthUiState.Idle
        }
    }

    fun logout(onComplete: (() -> Unit)? = null) {
        _uiState.value = AuthUiState.Idle
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState.Idle
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    fun deleteAccount(onResult: (Result<Unit>) -> Unit = {}) {
        _uiState.value = AuthUiState.Idle
        viewModelScope.launch {
            val result = repository.deleteAccount()
            _uiState.value = AuthUiState.Idle
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}
