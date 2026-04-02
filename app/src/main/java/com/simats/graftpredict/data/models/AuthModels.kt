package com.simats.graftpredict.data.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val user: String = "patient",
    val otp: String? = null
)

data class SignUpWithDetailsRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val dob: String,
    val gender: String,
    val user: String = "patient",
    val otp: String? = null
)

data class AuthResponse(
    val token: String?,
    val user_id: String?,
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val user: String?
)

data class SendOtpRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ResetPasswordRequest(
    val email: String,
    val new_password: String
)

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String
)

data class GenericResponse(
    val message: String?,
    val error: String?
)

data class UserDetailsData(
    val id: Long?,
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val user: String?,
    val dob: String?,
    val gender: String?,
    val age: Int?,
    val language: String?,
    val created_at: String?,
    val updated_at: String?
)

data class UserDetailsResponse(
    val user_details: UserDetailsData?,
    val error: String?
)
data class ShareReportRequest(
    val from_user_id: Long,
    val to_user_id: Long,
    val report_id: Int
)

data class SharedReport(
    val sno: Int?,
    val from_mail: String?,
    val to_mail: String?,
    val Date: String?,
    val reort_no: Int?,
    val from_user_id: Long?,
    val from_name: String?,
    val from_user_role: String?,
    val to_user_id: Long?,
    val to_name: String?,
    val to_user_role: String?
)

data class SharedReportsResponse(
    val shared_reports: List<SharedReport>?,
    val error: String?
)

data class SharedUser(
    val sno: Int?,
    val id: Long?,
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val user: String?
)

data class SharedUsersResponse(
    val shared_users: List<SharedUser>?,
    val error: String?
)

data class Doctor(
    val id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val dob: String?,
    val gender: String?,
    val age: Int?
)

data class DoctorsResponse(
    val doctors: List<Doctor>?,
    val error: String?
)

data class CreateDoctorRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val dob: String?,
    val gender: String?,
    val user: String = "doctor"
)

data class DoctorActionRequest(
    val doctor_id: String
)

data class UpdateUserDetailsRequest(
    val first_name: String?,
    val last_name: String?,
    val dob: String?,
    val gender: String?,
    val age: Int?,
    val language: String? = "English"
)
