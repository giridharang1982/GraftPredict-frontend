package com.example.graftpredict.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graftpredict.data.api.ApiClient
import com.example.graftpredict.data.local.SessionManager
import com.example.graftpredict.data.models.ReportData
import com.example.graftpredict.ui.theme.Manrope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ManageScreen(
    onBackClick: () -> Unit = {},
    onAddReportClick: () -> Unit = {},
    onReportClick: (reportId: String) -> Unit = {},
    onReceivedReportsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val apiService = remember(sessionManager) { ApiClient.create(sessionManager) }

    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var reportsList by remember { mutableStateOf<List<ReportData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var sentReportsList by rememberSaveable { mutableStateOf<List<SentReport>>(emptyList()) }
    var sentReportIds by rememberSaveable { mutableStateOf<Set<Int>>(emptySet()) }
    var isSentReportsLoading by remember { mutableStateOf(false) }
    var sentReportsError by remember { mutableStateOf("") }
    var sentReportsLoaded by rememberSaveable { mutableStateOf(false) }
    var showDeletePopup by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<SentReport?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var receivedReportsList by remember { mutableStateOf<List<SentReport>>(emptyList()) }
    var isReceivedReportsLoading by remember { mutableStateOf(false) }
    var receivedReportsError by remember { mutableStateOf("") }

    // Helper to fetch sent reports list (used in All reports and Sent tab)
    suspend fun refreshSentReports() {
        if (reportsList.isEmpty()) {
            // Don't load sent reports until reports are loaded
            return
        }
        if (sentReportsLoaded) {
            // Already loaded
            return
        }
        isSentReportsLoading = true
        sentReportsError = ""
        try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                sentReportsError = "Authentication required. Please log in again."
                sentReportsList = emptyList()
                sentReportIds = emptySet()
                return
            }

            println("[DEBUG] Fetching sent reports with token: $token")
            val response = apiService.getSentSharedReports()
            println("[DEBUG] Sent reports response: ${response.shared_reports?.size ?: 0} reports")
            val currentUserEmail = sessionManager.getEmail() ?: ""
            println("[DEBUG] Current user email: $currentUserEmail")

            // Build a map of reports by ID for quick lookup (if list already loaded)
            val reportsById = reportsList.filterNotNull().associateBy { it.report_id }

            val filteredSent = response.shared_reports?.filter { report ->
                report.from_mail == currentUserEmail
            } ?: emptyList()

            sentReportIds = filteredSent.mapNotNull { it.reort_no }.toSet()

            sentReportsList = filteredSent.mapNotNull { report ->
                val reportData = report.reort_no?.let { reportsById[it] }
                if (reportData != null) {
                    SentReport(
                        id = report.sno ?: 0,
                        recipientName = report.to_name ?: report.to_mail ?: "Unknown",
                        recipientType = report.to_user_role ?: "User",
                        recipientId = report.to_user_id?.toString() ?: "",
                        patientName = reportData.name ?: "N/A",
                        stValue = reportData.predicted_st_value?.toString()?.toDoubleOrNull() ?: 0.0,
                        graftValue = reportData.graft_diameter_1?.toString()?.toDoubleOrNull() ?: 0.0,
                        date = report.Date ?: "N/A",
                        reportId = report.reort_no ?: 0,
                        fromEmail = report.from_mail ?: "",
                        toEmail = report.to_mail ?: ""
                    )
                } else null
            }

            sentReportsLoaded = true

            if (sentReportsList.isEmpty()) {
                sentReportsError = ""
            }
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                401 -> sentReportsError = "Session expired. Please log in again."
                403 -> sentReportsError = "You don't have permission to access sent reports."
                429 -> {
                    sentReportsError = "Too many requests. Sent reports will be available later."
                    sentReportsLoaded = true // Prevent further attempts
                }
                else -> sentReportsError = "Error loading reports: ${e.message()}"
            }
            sentReportsList = emptyList()
            sentReportIds = emptySet()
            e.printStackTrace()
        } catch (e: Exception) {
            sentReportsError = "Error loading reports: ${e.localizedMessage ?: e.message ?: "Unknown error"}"
            sentReportsList = emptyList()
            sentReportIds = emptySet()
            e.printStackTrace()
        } finally {
            isSentReportsLoading = false
        }
    }

    // LaunchedEffect for loading received reports when tab changes
    LaunchedEffect(selectedTab) {
        if (selectedTab == 2) {
            isReceivedReportsLoading = true
            receivedReportsError = ""
            try {
                val token = sessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    receivedReportsError = "Authentication required. Please log in again."
                    receivedReportsList = emptyList()
                    return@LaunchedEffect
                }

                println("[DEBUG] Fetching received reports with token: $token")
                val response = apiService.getSharedReports()
                println("[DEBUG] Received reports response: ${response.shared_reports?.size ?: 0} reports")
                response.shared_reports?.forEach { report ->
                    println("[DEBUG] Report: sno=${report.sno}, from_mail=${report.from_mail}, to_mail=${report.to_mail}, reort_no=${report.reort_no}")
                }
                val currentUserEmail = sessionManager.getEmail() ?: ""
                println("[DEBUG] Current user email: $currentUserEmail")

                receivedReportsList = response.shared_reports?.mapNotNull { report ->
                    if (report.to_mail == currentUserEmail) {
                        try {
                            val senderResponse = apiService.getUserById(report.from_user_id ?: 0)
                            val reportResponse = apiService.getReportById(report.reort_no ?: 0)
                            val reportData = reportResponse.report

                            if (senderResponse.user_details != null && reportData != null) {
                                SentReport(
                                    id = report.sno ?: 0,
                                    recipientName = "${senderResponse.user_details.first_name ?: ""} ${senderResponse.user_details.last_name ?: ""}".trim(),
                                    recipientType = senderResponse.user_details.user ?: "User",
                                    recipientId = senderResponse.user_details.id?.toString() ?: "",
                                    patientName = reportData.name ?: "N/A",
                                    stValue = (reportData.predicted_st_value?.toString()?.toDoubleOrNull()) ?: 0.0,
                                    graftValue = (reportData.graft_diameter_1?.toString()?.toDoubleOrNull()) ?: 0.0,
                                    date = report.Date ?: "N/A",
                                    reportId = report.reort_no ?: 0,
                                    fromEmail = report.from_mail ?: "",
                                    toEmail = report.to_mail ?: ""
                                )
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    } else {
                        null
                    }
                } ?: emptyList()

                if (receivedReportsList.isEmpty()) {
                    receivedReportsError = ""
                }
            } catch (e: retrofit2.HttpException) {
                when (e.code()) {
                    401 -> receivedReportsError = "Session expired. Please log in again."
                    403 -> receivedReportsError = "You don't have permission to access received reports."
                    else -> receivedReportsError = "Error loading reports: ${e.message()}"
                }
                receivedReportsList = emptyList()
                e.printStackTrace()
            } catch (e: Exception) {
                receivedReportsError = "Error loading reports: ${e.localizedMessage ?: e.message ?: "Unknown error"}"
                receivedReportsList = emptyList()
                e.printStackTrace()
            } finally {
                isReceivedReportsLoading = false
            }
        }
    }

    // Coroutine scope for delete operation
    val scope = rememberCoroutineScope()

    // Load reports on screen init
    LaunchedEffect(Unit) {
        try {
            val response = apiService.listReports()
            if (response.reports != null) {
                reportsList = response.reports
                // Load sent reports if needed
                if (selectedTab == 0 || selectedTab == 1) {
                    refreshSentReports()
                }
            } else {
                errorMessage = response.error ?: "Failed to load reports"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Color palette from HTML
    val backgroundColor = Color(0xFF101922)
    val backgroundLight = Color(0xFFF6F7F8)
    val surfaceDark = Color(0xFF192633)
    val borderDark = Color(0xFF324D67)
    val textSecondary = Color(0xFF92ADC9)
    val primaryColor = Color(0xFF137FEC)
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val borderLight = Color(0xFFE2E8F0)
    val surfaceLight = Color.White

    // Dark mode (as per HTML)
    val isDarkMode = true

    Scaffold(
        containerColor = if (isDarkMode) backgroundColor else backgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                    top = 0.dp,
                    end = paddingValues.calculateRightPadding(LocalLayoutDirection.current),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            // Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                color = (if (isDarkMode) backgroundColor else backgroundLight).copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = if (isDarkMode) textPrimary else textPrimaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Title
                    Text(
                        text = "Manage Reports",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.015).sp,
                        color = if (isDarkMode) textPrimary else textPrimaryDark
                    )

                    // Add Button
                    IconButton(
                        onClick = onAddReportClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = "Add Report",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Main Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Search Bar (Sticky)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        color = if (isDarkMode) backgroundColor else backgroundLight
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Search Icon
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = if (isDarkMode) borderDark else Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterStart)
                                    .padding(start = 12.dp)
                            )

                            // Text Field
                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isDarkMode) surfaceDark else surfaceLight,
                                    unfocusedContainerColor = if (isDarkMode) surfaceDark else surfaceLight,
                                    disabledContainerColor = if (isDarkMode) surfaceDark else surfaceLight,
                                    focusedIndicatorColor = primaryColor,
                                    unfocusedIndicatorColor = if (isDarkMode) borderDark else borderLight,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedTextColor = if (isDarkMode) textPrimary else textPrimaryDark,
                                    unfocusedTextColor = if (isDarkMode) textPrimary else textPrimaryDark,
                                    cursorColor = primaryColor,
                                ),
                                placeholder = {
                                    Text(
                                        text = "Search reports...",
                                        fontFamily = Manrope,
                                        fontSize = 14.sp,
                                        color = if (isDarkMode) textSecondary.copy(alpha = 0.5f) else Color(0xFF94A3B8)
                                    )
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = Manrope,
                                    fontSize = 14.sp
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    // Tab Segmented Control
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        color = if (isDarkMode) surfaceDark.copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isDarkMode) borderDark else borderLight)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // All Reports Tab (Selected)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        color = if (selectedTab == 0) {
                                            if (isDarkMode) primaryColor else Color.White
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTab = 0 }
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "All Reports",
                                    fontFamily = Manrope,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (selectedTab == 0) {
                                        if (isDarkMode) textPrimary else textPrimaryDark
                                    } else {
                                        if (isDarkMode) textSecondary else Color(0xFF64748B)
                                    }
                                )
                            }

                            // Sent Reports Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        color = if (selectedTab == 1) {
                                            if (isDarkMode) primaryColor else Color.White
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTab = 1 }
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sent Reports",
                                    fontFamily = Manrope,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (selectedTab == 1) {
                                        if (isDarkMode) textPrimary else textPrimaryDark
                                    } else {
                                        if (isDarkMode) textSecondary else Color(0xFF64748B)
                                    }
                                )
                            }

                            // Received Reports Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        color = if (selectedTab == 2) {
                                            if (isDarkMode) primaryColor else Color.White
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTab = 2 }
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Received Reports",
                                    fontFamily = Manrope,
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (selectedTab == 2) {
                                        if (isDarkMode) textPrimary else textPrimaryDark
                                    } else {
                                        if (isDarkMode) textSecondary else Color(0xFF64748B)
                                    }
                                )
                            }
                        }
                    }
                }

                // Report Cards
                item {
                    // Show different content based on selectedTab
                    if (selectedTab == 1) {
                        // Sent Reports Tab - Show dynamically fetched sent reports
                        if (isSentReportsLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = primaryColor)
                            }
                        } else if (sentReportsError.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sentReportsError,
                                    fontFamily = Manrope,
                                    fontSize = 13.sp,
                                    color = Color(0xFFEF4444),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (sentReportsList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No sent reports",
                                    fontFamily = Manrope,
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                sentReportsList.forEach { report ->
                                    SentReportCard(
                                        report = report,
                                        onDeleteClick = {
                                            reportToDelete = report
                                            showDeletePopup = true
                                        },
                                        onCardClick = {
                                            onReportClick(report.reportId.toString())
                                        }
                                    )
                                }
                            }
                        }
                    } else if (selectedTab == 2) {
                        // Received Reports Tab - Show dynamically fetched received reports
                        if (isReceivedReportsLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = primaryColor)
                            }
                        } else if (receivedReportsError.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = receivedReportsError,
                                    fontFamily = Manrope,
                                    fontSize = 13.sp,
                                    color = Color(0xFFEF4444),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (receivedReportsList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No received reports",
                                    fontFamily = Manrope,
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                receivedReportsList.forEach { report ->
                                    SentReportCard(
                                        report = report,
                                        onDeleteClick = { /* Handle delete if needed */ },
                                        onCardClick = {
                                            onReportClick(report.reportId.toString())
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // All Reports Tab (selectedTab == 0) - Show dynamically created reports from API
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = primaryColor)
                            }
                        } else if (errorMessage.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    fontFamily = Manrope,
                                    fontSize = 14.sp
                                )
                            }
                        } else if (reportsList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No reports available",
                                    color = textSecondary,
                                    fontFamily = Manrope,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Dynamically create report cards from API
                                val filteredReports = if (searchText.isBlank()) reportsList else reportsList.filter { it.name?.contains(searchText, ignoreCase = true) == true }
                                filteredReports.forEachIndexed { index, report ->
                                    val formattedDate = formatDateForDisplay(report.submission_time)
                                    val reportNumber = report.report_id ?: (index + 1)

                                    // Calculate ST value from posterior and lateral measurements
                                    val posteriorST = report.posterior_st
                                    val lateralST = report.lateral_st
                                    val stValue = (posteriorST + lateralST) / 2.0  // Mean of posterior and lateral ST
                                    val stLength = stValue.toInt().toString()
                                    
                                    // Convert graft_diameter_1 safely (it comes as Any? type)
                                    val d1Value = when (val d1 = report.graft_diameter_1) {
                                        is Number -> d1.toDouble()
                                        is String -> d1.toDoubleOrNull() ?: 0.0
                                        else -> 0.0
                                    }
                                    val graftD1 = String.format("%.1f", d1Value)
                                    val patientName = report.name?.trim() ?: "Unknown Patient"
                                    val status = if (report.report_id != null && sentReportIds.contains(report.report_id)) "SENT" else ""

                                val statusColor = when (status.uppercase()) {
                                    "SENT" -> Color(0xFF10B981) // Green
                                    "VIEWED" -> Color(0xFF3B82F6) // Blue
                                    "DRAFT" -> Color(0xFF94A3B8) // Gray
                                    else -> Color.Transparent
                                }

                                    val statusBackground = if (status.isNotBlank()) statusColor.copy(alpha = 0.1f) else Color.Transparent

                                    ReportCard(
                                        title = "Graft Size Report",
                                        subtitle = formattedDate,
                                        icon = Icons.Outlined.Analytics,
                                        iconColor = primaryColor,
                                        iconBackground = primaryColor.copy(alpha = 0.1f),
                                        status = status,
                                        statusColor = statusColor,
                                        statusBackground = statusBackground,
                                        dataItems = listOf(
                                            Pair("ST", "$stLength mm"),
                                            Pair("D1", "$graftD1 mm")
                                        ),
                                        patientName = patientName,
                                        arrowIcon = Icons.Outlined.ArrowForward,
                                        isDarkMode = isDarkMode,
                                        onClick = { onReportClick(report.report_id.toString()) }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Delete confirmation popup for sent reports
    if (showDeletePopup && reportToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeletePopup = false },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Remove Access",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "This report is currently shared with ${reportToDelete!!.recipientName}. Remove access?",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF92ADC9),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showDeletePopup = false },
                        enabled = !isDeleting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF324D67),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel", fontFamily = Manrope)
                    }
                    Button(
                        onClick = {
                            if (reportToDelete != null) {
                                isDeleting = true
                                scope.launch {
                                    try {
                                        val response = apiService.deleteSharedReport(reportToDelete!!.id)
                                        if (response.message?.contains("deleted") == true || response.message?.contains("success") == true) {
                                            sentReportsList = sentReportsList.filter { it.id != reportToDelete!!.id }
                                            showDeletePopup = false
                                            reportToDelete = null
                                        }
                                    } catch (e: Exception) {
                                        println("[ERROR] Failed to delete shared report: ${e.message}")
                                        e.printStackTrace()
                                    } finally {
                                        isDeleting = false
                                    }
                                }
                            }
                        },
                        enabled = !isDeleting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        )
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Remove", fontFamily = Manrope)
                        }
                    }
                }
            },
            containerColor = Color(0xFF192633),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun ReportCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBackground: Color,
    status: String,
    statusColor: Color,
    statusBackground: Color,
    dataItems: List<Pair<String, String>>, // Changed to Pair for label and value
    patientName: String,
    arrowIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkMode: Boolean,
    isDraft: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (isDarkMode) Color(0xFF192633) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF324D67) else Color(0xFFE2E8F0)
    val borderDark = Color(0xFF324D67)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF92ADC9) else Color(0xFF64748B)
    val cardBackground = if (isDarkMode) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFF8FAFC)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with icon, title, and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon and Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                        Text(
                            text = subtitle,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }

                // Status Badge
                if (status.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBackground)
                            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status.uppercase(),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = statusColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Data Section - HTML-like grid
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, if (isDarkMode) borderDark.copy(alpha = 0.3f) else Color(0xFFE2E8F0))
            ) {
                // Grid layout similar to HTML
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    dataItems.forEachIndexed { index, (label, value) ->
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = value,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer with patient name and arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Patient Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Patient",
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = patientName,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isDraft) textSecondary.copy(alpha = 0.5f) else textSecondary
                    )
                }

                // Arrow Icon
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = "View",
                    tint = if (isDarkMode) borderDark else Color(0xFFCBD5E1),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Format submission_time from database (e.g., "2023-10-24 15:30:45") to display format (e.g., "#2023-084 • Oct 24, 2023")
 */
fun formatDateForDisplay(submissionTime: String): String {
    return try {
        // Parse the database datetime format (YYYY-MM-DD HH:MM:SS)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(submissionTime) ?: return submissionTime

        // Format to display format (e.g., "Oct 24, 2023")
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = outputFormat.format(date)

        // Generate a simple report ID (using day of year as example)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val reportId = String.format("%04d-%03d", year, dayOfYear)

        "$reportId • $formattedDate"
    } catch (e: Exception) {
        submissionTime
    }
}
