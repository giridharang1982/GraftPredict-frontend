package com.simats.graftpredict.ui.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.graftpredict.ui.theme.Manrope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.simats.graftpredict.data.api.ApiClient
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.data.models.ReportData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(
    onBackClick: () -> Unit = {},
    onCreateNewClick: () -> Unit = {},
    onReportClick: (reportId: Int) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Colors from HTML
    val backgroundColor = Color(0xFF0B1219) // background-dark
    val surfaceColor = Color(0xFF162330) // surface-dark
    val borderColor = Color(0xFF2A3D50) // border-dark
    val primaryColor = Color(0xFF137FEC) // primary
    val textSecondary = Color(0xFF92ADC9) // text-secondary
    val textPrimary = Color.White
    val lightSurfaceColor = Color.White
    val lightBorderColor = Color(0xFFE2E8F0)
    val lightTextSecondary = Color(0xFF64748B)

    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val apiService = remember(sessionManager) { ApiClient.create(sessionManager) }
    val scope = rememberCoroutineScope()

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for delete dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<ReportItem?>(null) }

    // State for loading reports
    var reportsList by remember { mutableStateOf<List<ReportData>>(emptyList()) }
    var isLoadingReports by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }

    // Fetch reports from API
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = apiService.listReports()
                val reports = response.reports ?: emptyList()
                withContext(Dispatchers.Main) {
                    reportsList = reports
                    isLoadingReports = false
                }
            } catch (e: Exception) {
                println("[ERROR] Failed to load reports: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isLoadingReports = false
                }
            }
        }
    }

    // Convert API reports to ReportItem for display
    val reports = reportsList.mapIndexed { index, report ->
        val formattedDate = formatDateForDisplay(report.submission_time)
        
        // Calculate ST value from posterior and lateral measurements
        val posteriorST = report.posterior_st
        val lateralST = report.lateral_st
        val stValue = ((posteriorST + lateralST) / 2.0).toInt()
        
        // Convert graft_diameter_1 safely (it comes as Any? type)
        val d1Value = when (val d1 = report.graft_diameter_1) {
            is Number -> d1.toDouble()
            is String -> d1.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        val patientName = report.name.trim().ifEmpty { "Unknown Patient" }

        ReportItem(
            id = report.report_id,
            patientName = patientName,
            email = report.email,
            date = formattedDate,
            stValue = stValue,
            d1Value = d1Value,
            d2Value = (report.graft_diameter_2 as? Number)?.toDouble() ?: 0.0,
            hamstringValue = 0.0,
            quadValue = 0.0
        )
    }

    // Filter reports based on search by name or email
    val filteredReports = if (searchQuery.isBlank()) {
        reports
    } else {
        reports.filter { report ->
            report.patientName.contains(searchQuery, ignoreCase = true) ||
            report.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
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
                    .height(56.dp),
                color = backgroundColor,
                border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Title
                    Text(
                        text = "Manage Reports",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.015).sp,
                        color = textPrimary
                    )

                    // Add Button
                    IconButton(
                        onClick = onCreateNewClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
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

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = surfaceColor.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = textSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = Manrope,
                                fontSize = 14.sp,
                                color = textPrimary
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search patients...",
                                            fontFamily = Manrope,
                                            fontSize = 14.sp,
                                            color = textSecondary.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }

            // Reports List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                )
            ) {
                items(filteredReports) { report ->
                    ReportCard(
                        report = report,
                        onDeleteClick = {
                            reportToDelete = report
                            showDeleteDialog = true
                        },
                        onClick = { onReportClick(report.id) }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        Dialog(
            onDismissRequest = { showDeleteDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .width(280.dp),
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dialog Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Warning Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = "Delete",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
                        Text(
                            text = "Delete Report?",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        Text(
                            text = "This action cannot be undone. This patient report will be permanently removed.",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(borderColor.copy(alpha = 0.5f))
                    )

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel Button
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                reportToDelete = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = textSecondary
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(56.dp)
                                .background(borderColor.copy(alpha = 0.5f))
                        )

                        // Delete Button
                        TextButton(
                            onClick = {
                                if (reportToDelete != null) {
                                    isDeleting = true
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val response = apiService.deleteReport(reportToDelete!!.id)
                                            withContext(Dispatchers.Main) {
                                                if (response.error == null) {
                                                    // Remove from list
                                                    reportsList = reportsList.filter { it.report_id != reportToDelete!!.id }
                                                    showDeleteDialog = false
                                                    reportToDelete = null
                                                } else {
                                                    println("[ERROR] Failed to delete report: ${response.error}")
                                                }
                                                isDeleting = false
                                            }
                                        } catch (e: Exception) {
                                            println("[ERROR] Error deleting report: ${e.message}")
                                            e.printStackTrace()
                                            withContext(Dispatchers.Main) {
                                                isDeleting = false
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isDeleting,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFDC2626),
                                disabledContentColor = Color(0xFFDC2626).copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = if (isDeleting) "Deleting..." else "Delete",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    report: ReportItem,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    val surfaceColor = Color(0xFF162330) // surface-dark
    val borderColor = Color(0xFF2A3D50) // border-dark
    val textSecondary = Color(0xFF92ADC9) // text-secondary
    val textPrimary = Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Date in top right corner
            Text(
                text = report.date,
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = textSecondary.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Main content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar/Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF137FEC).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Patient",
                        tint = Color(0xFF137FEC),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Report Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Patient Name
                    Text(
                        text = report.patientName,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textPrimary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Metrics
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ST Value
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "ST",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                color = textSecondary.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${report.stValue}mm",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        // D1 Value
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "D1",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                color = textSecondary.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${report.d1Value}mm",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Delete Button and Chevron
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Delete Button
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Chevron
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "View Details",
                        tint = textSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

data class ReportItem(
    val id: Int,
    val patientName: String,
    val email: String,
    val date: String,
    val stValue: Int,
    val d1Value: Double,
    val d2Value: Double,
    val hamstringValue: Double,
    val quadValue: Double
)

fun formatDateForDisplay(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Unknown Date"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date).toLowerCase()
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

// Preview for testing
@Composable
fun ReportScreenPreview() {
    ReportScreen(
        onBackClick = {},
        onCreateNewClick = {},
        onReportClick = {},
        onProfileClick = {}
    )
}
