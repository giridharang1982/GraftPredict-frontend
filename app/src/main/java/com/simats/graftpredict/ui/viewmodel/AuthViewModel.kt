package com.simats.graftpredict.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.data.repository.AuthRepository
import com.simats.graftpredict.utils.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String? = null) : UiState()
    data class Error(val error: String) : UiState()
}

class AuthViewModel(private val repo: AuthRepository, private val sessionManager: SessionManager) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.login(email, password)
            if (res.isSuccess) {
                val r = res.getOrNull()
                if (!r?.token.isNullOrEmpty()) {
                    sessionManager.saveSession(r!!.token!!, r.user_id, r.first_name, r.last_name, r.email, r.user)
                    _state.value = UiState.Success("Logged in")
                } else {
                    _state.value = UiState.Error("Missing token")
                }
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "Login failed"))
            }
        }
    }

    fun sendForgotOtp(email: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.sendForgotOtp(email)
            if (res.isSuccess) {
                _state.value = UiState.Success(res.getOrNull()?.message)
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "Failed to send OTP"))
            }
        }
    }

    fun verifyForgotOtp(email: String, otp: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.verifyForgotOtp(email, otp)
            if (res.isSuccess) {
                _state.value = UiState.Success(res.getOrNull()?.message)
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "OTP verification failed"))
            }
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.resetPassword(email, newPassword)
            if (res.isSuccess) {
                _state.value = UiState.Success(res.getOrNull()?.message)
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "Reset failed"))
            }
        }
    }

    fun signupSendOtp(email: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.signupSendOtp(email)
            if (res.isSuccess) {
                _state.value = UiState.Success(res.getOrNull()?.message)
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "Failed to send OTP"))
            }
        }
    }

    fun signup(firstName: String, lastName: String, email: String, password: String, otp: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.signup(
                com.simats.graftpredict.data.models.SignUpRequest(
                    first_name = firstName,
                    last_name = lastName,
                    email = email,
                    password = password,
                    user = "patient",
                    otp = otp
                )
            )
            if (res.isSuccess) {
                val r = res.getOrNull()
                // For signup we only need to know it succeeded; backend may or may not return a token.
                if (r?.token != null && r.token!!.isNotEmpty()) {
                    sessionManager.saveSession(r.token, r.user_id, r.first_name, r.last_name, r.email, r.user)
                }
                _state.value = UiState.Success("Sign up successful")
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "Signup failed"))
            }
        }
    }

    fun signupWithDetails(firstName: String, lastName: String, email: String, password: String, dateOfBirth: String, gender: String, otp: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = repo.signupWithDetails(
                com.simats.graftpredict.data.models.SignUpWithDetailsRequest(
                    first_name = firstName,
                    last_name = lastName,
                    email = email,
                    password = password,
                    dob = dateOfBirth,
                    gender = gender,
                    user = "patient",
                    otp = otp
                )
            )
            if (res.isSuccess) {
                val r = res.getOrNull()
                // For signup we only need to know it succeeded; backend may or may not return a token.
                if (r?.token != null && r.token!!.isNotEmpty()) {
                    sessionManager.saveSession(r.token, r.user_id, r.first_name, r.last_name, r.email, r.user)
                }
                _state.value = UiState.Success("Sign up successful")
            } else {
                _state.value = UiState.Error(ErrorHandler.sanitizeError(res.exceptionOrNull() as? Exception, "Signup failed"))
            }
        }
    }
}
