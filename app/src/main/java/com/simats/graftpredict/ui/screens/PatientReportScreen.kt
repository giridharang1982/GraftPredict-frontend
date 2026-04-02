package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.theme.Manrope
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.simats.graftpredict.data.api.ApiClient
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.data.models.ReportData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientsReportsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onReportClick: (reportId: Int) -> Unit = {}
) {
    // Colors from HTML
    val backgroundColor = Color(0xFF101922) // Match HomeScreen background
    val surfaceDark = Color(0xFF15202B) // surface-dark
    val borderDark = Color(0xFF2D3E4F) // border-dark
    val primaryBlue = Color(0xFF137FEC) // primary
    val medicalTeal = Color(0xFF2DD4BF) // medical-teal
    val medicalBlue = Color(0xFF4FACFE) // medical-blue
    val textSecondary = Color(0xFF94A3B8) // text-secondary
    val femalePink = Color(0xFFEC4899) // female-pink
    val maleBlue = Color(0xFF137FEC) // male-blue
    val white = Color.White

    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val apiService = remember(sessionManager) { ApiClient.create(sessionManager) }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var reportsList by remember { mutableStateOf<List<ReportData>>(emptyList()) }
    var isLoadingReports by remember { mutableStateOf(true) }

    // Fetch reports from API - All reports from all users
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getAllReports()
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

    // Filter reports based on search by name or email
    val filteredReports = if (searchQuery.isBlank()) {
        reportsList
    } else {
        reportsList.filter { report ->
            report.name.contains(searchQuery, ignoreCase = true) ||
            report.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 12.dp)
                    .border(
                        width = 1.dp,
                        color = borderDark.copy(alpha = 0.5f),
                        shape = androidx.compose.ui.graphics.RectangleShape
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable(onClick = onBackClick)
                        .background(surfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = white,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title
                Text(
                    text = "Patients Reports",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    color = white,
                    letterSpacing = (-0.025).sp
                )

                // Empty space for alignment
                Box(modifier = Modifier.size(32.dp))
            }
        },
        bottomBar = {} // Empty - no bottom navigation bar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(surfaceDark)
                        .border(1.dp, borderDark, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp),
                            textStyle = TextStyle(
                                color = white,
                                fontSize = 14.sp,
                                fontFamily = Manrope
                            ),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search by name or email...",
                                        fontFamily = Manrope,
                                        fontSize = 12.sp,
                                        color = textSecondary.copy(alpha = 0.5f)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }

            // Loading state
            if (isLoadingReports) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            } else if (filteredReports.isEmpty()) {
                // No reports found
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AssignmentLate,
                            contentDescription = "No reports",
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No reports found",
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Patient Reports List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredReports) { report ->
                        PatientReportCard(
                            report = report,
                            backgroundColor = surfaceDark,
                            borderColor = borderDark,
                            textPrimary = white,
                            textSecondary = textSecondary,
                            maleColor = maleBlue,
                            femaleColor = femalePink,
                            medicalBlue = medicalBlue,
                            medicalTeal = medicalTeal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onReportClick(report.report_id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun PatientReportCard(
    report: ReportData,
    backgroundColor: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    maleColor: Color,
    femaleColor: Color,
    medicalBlue: Color,
    medicalTeal: Color,
    modifier: Modifier = Modifier
) {
    val genderColor = if (report.gender.equals("male", ignoreCase = true)) maleColor else femaleColor
    val formattedDate = formatPatientReportDate(report.affected_date ?: report.submission_time)
    
    // Calculate ST value from posterior and lateral measurements
    val posteriorST = report.posterior_st
    val lateralST = report.lateral_st
    val stValue = (posteriorST + lateralST) / 2.0  // Mean of posterior and lateral ST
    val stLength = stValue.toInt()
    
    // Calculate S1 (Hamstring) value from measurements
    val posteriorGracilis = report.posterior_gracilis
    val lateralGracilis = report.lateral_gracilis
    val s1Value = (posteriorGracilis + lateralGracilis) / 2.0  // Mean of gracilis measurements
    val s1Length = String.format("%.1f", s1Value)
    
    // Convert graft_diameter_1 safely (it comes as Any? type)
    val graftSize = when (val d1 = report.graft_diameter_1) {
        is Number -> d1.toDouble()
        is String -> d1.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Name and Age row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Name with icon
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Person",
                        tint = genderColor,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = report.name,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Age
                Text(
                    text = "${report.age}y",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = textSecondary
                )
            }

            // Knee side and date row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Knee side
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AirlineSeatLegroomExtra,
                        contentDescription = "Knee",
                        tint = medicalBlue,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = report.affected_side,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }

                // Date
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Date",
                        tint = textSecondary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = formattedDate,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }
            }

            // Divider and measurements
            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Measurements row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ST Length
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ST:",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = medicalBlue.copy(alpha = 0.8f),
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stLength.toString(),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = textPrimary
                    )

                    Text(
                        text = "mm",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 9.sp,
                        color = textPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                // Vertical divider
                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // S1 (Gracilis) Length
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S1:",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = medicalTeal,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = s1Length,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = textPrimary
                    )

                    Text(
                        text = "mm",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 9.sp,
                        color = textPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                // Vertical divider
                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Graft size
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Graft:",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = medicalTeal,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = String.format("%.1f", graftSize),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = textPrimary
                    )

                    Text(
                        text = "mm",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 9.sp,
                        color = textPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

fun formatPatientReportDate(dateString: String): String {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = dateFormat.parse(dateString) ?: return dateString
        SimpleDateFormat("dd MMM yyyy", Locale.US).format(date)
    } catch (e: Exception) {
        dateString
    }
}
