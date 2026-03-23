package com.example.graftpredict.data.models

import com.google.gson.annotations.SerializedName
/**
 * Report data model matching app_report table structure
 */
data class GraftReport(
    val name: String,
    val age: Int,
    val gender: String,
    val affected_side: String,
    val affected_date: String,
    val height: Double,
    val weight: Double,
    val bmi: Double,
    val leg_length: Double,
    val thigh_length: Double,
    @SerializedName("circumfrence_thigh")
    val circumference_thigh: Double,
    val posterior_st: Double = 0.0,
    val posterior_gracilis: Double = 0.0,
    val lateral_st: Double = 0.0,
    val lateral_gracilis: Double = 0.0,
    val graft_diameter_1: Double = 0.0,
    val graft_diameter_2: Double? = null,
    val hamstring_autograft: Double? = null,
    val quadriceps_tendon_diameter: Double? = null,
    val minimum_st_length: Double? = null,
    val predicted_st_value: Double? = null,
    val gracilis_length: Double? = null
)

/**
 * API Response for report creation
 */
data class ReportResponse(
    val message: String? = null,
    val report_id: Int? = null,
    val error: String? = null
)

/**
 * API Response for report retrieval (list)
 */
data class ReportListResponse(
    val reports: List<ReportData>? = null,
    val error: String? = null
)

/**
 * API Response for single report retrieval
 */
data class SingleReportResponse(
    val report: ReportData? = null,
    val error: String? = null
)




data class ReportData(
    @SerializedName("report_id")
    val report_id: Int,

    @SerializedName("user_id")
    val user_id: Long,

    @SerializedName("email")
    val email: String,

    @SerializedName("Name") // Capital N to match API
    val name: String,

    @SerializedName("Age")
    val age: Int,

    @SerializedName("Gender")
    val gender: String,

    @SerializedName("affected_side")
    val affected_side: String,

    @SerializedName("affected_date")
    val affected_date: String?,

    @SerializedName("height")
    val height: Double,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("bmi")
    val bmi: Double,

    @SerializedName("leg_length")
    val leg_length: Double,

    @SerializedName("thigh_length")
    val thigh_length: Double,

    @SerializedName("circumfrence_thigh")
    val circumfrence_thigh: Double,

    @SerializedName("posterior_st")
    val posterior_st: Double,

    @SerializedName("posterior_gracilis")
    val posterior_gracilis: Double,

    @SerializedName("lateral_st")
    val lateral_st: Double,

    @SerializedName("lateral_gracilis")
    val lateral_gracilis: Double,

    @SerializedName("submission_time")
    val submission_time: String,

    @SerializedName("graft_diameter_1")
    val graft_diameter_1: Any? = null,

    @SerializedName("graft_diameter_2")
    val graft_diameter_2: Any? = null,

    @SerializedName("hamstring_autograft")
    val hamstring_autograft: Any? = null,

    @SerializedName("quadriceps_tendon_diameter")
    val quadriceps_tendon_diameter: Any? = null,

    @SerializedName("minimum_st_length")
    val minimum_st_length: Any? = null,

    @SerializedName("predicted_st_value")
    val predicted_st_value: Any? = null,

    @SerializedName("gracilis_length")
    val gracilis_length: Any? = null
)
