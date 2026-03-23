package com.example.graftpredict.data.repository

import com.example.graftpredict.data.api.ApiService
import com.example.graftpredict.data.models.*

class AuthRepository(private val api: ApiService) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val res = api.login(LoginRequest(email, password))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendForgotOtp(email: String): Result<GenericResponse> {
        return try {
            val res = api.forgotPasswordSendOtp(SendOtpRequest(email))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyForgotOtp(email: String, otp: String): Result<GenericResponse> {
        return try {
            val res = api.forgotPasswordVerifyOtp(VerifyOtpRequest(email, otp))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<GenericResponse> {
        return try {
            val res = api.forgotPasswordReset(ResetPasswordRequest(email, newPassword))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signupSendOtp(email: String): Result<GenericResponse> {
        return try {
            val res = api.signupSendOtp(SendOtpRequest(email))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(request: SignUpRequest): Result<AuthResponse> {
        return try {
            val res = api.signup(request)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signupWithDetails(request: SignUpWithDetailsRequest): Result<AuthResponse> {
        return try {
            val res = api.signupWithDetails(request)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserDetails(token: String): Result<UserDetailsResponse> {
        return try {
            val res = api.getUserDetails()
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
