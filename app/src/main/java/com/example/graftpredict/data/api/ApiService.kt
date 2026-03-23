package com.example.graftpredict.data.api

import com.example.graftpredict.data.models.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE

interface ApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/signup")
    suspend fun signup(@Body request: SignUpRequest): AuthResponse

    @POST("/signup")
    suspend fun signupWithDetails(@Body request: SignUpWithDetailsRequest): AuthResponse

    @POST("/forgot-password/send-otp")
    suspend fun forgotPasswordSendOtp(@Body request: SendOtpRequest): GenericResponse

    @POST("/forgot-password/verify-otp")
    suspend fun forgotPasswordVerifyOtp(@Body request: VerifyOtpRequest): GenericResponse

    @POST("/forgot-password/reset-password")
    suspend fun forgotPasswordReset(@Body request: ResetPasswordRequest): GenericResponse

    @POST("/signup/send-otp")
    suspend fun signupSendOtp(@Body request: SendOtpRequest): GenericResponse

    @POST("/reports")
    suspend fun createReport(
        @Body report: GraftReport
    ): ReportResponse

    @GET("/reports")
    suspend fun listReports(): ReportListResponse

    @GET("/all_reports")
    suspend fun getAllReports(): ReportListResponse

    @GET("/reports/{reportId}")
    suspend fun getReportById(@Path("reportId") reportId: Int): SingleReportResponse

    @GET("/user/details")
    suspend fun getUserDetails(): UserDetailsResponse

    @PUT("/user/details/update")
    suspend fun updateUserDetails(@Body request: com.example.graftpredict.data.models.UpdateUserDetailsRequest): GenericResponse

    // Share Report endpoints
    @POST("/share_report")
    suspend fun shareReport(
        @Body request: ShareReportRequest
    ): GenericResponse

    @GET("/share_report/{reportId}/{fromUserId}")
    suspend fun getSharedUsersForReport(
        @Path("reportId") reportId: Int,
        @Path("fromUserId") fromUserId: Long
    ): SharedUsersResponse

    @GET("/shared_reports")
    suspend fun getSharedReports(): SharedReportsResponse

    @GET("/shared_reports/sent")
    suspend fun getSentSharedReports(): SharedReportsResponse

    @DELETE("/shared_reports/{sno}")
    suspend fun deleteSharedReport(
        @Path("sno") sno: Int
    ): GenericResponse

    @GET("/search/user/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Long
    ): UserDetailsResponse

    @DELETE("/reports/{reportId}")
    suspend fun deleteReport(
        @Path("reportId") reportId: Int
    ): GenericResponse

    @POST("/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): GenericResponse

    @DELETE("/delete-account")
    suspend fun deleteAccount(): GenericResponse

    @GET("/admin/doctors")
    suspend fun getDoctors(): DoctorsResponse

    @POST("/admin/create-doctor")
    suspend fun createDoctor(@Body request: CreateDoctorRequest): GenericResponse

    @POST("/admin/doctor/restrict")
    suspend fun restrictDoctor(@Body request: DoctorActionRequest): GenericResponse

    @POST("/admin/doctor/activate")
    suspend fun activateDoctor(@Body request: DoctorActionRequest): GenericResponse

    // Add other endpoints as needed
}
