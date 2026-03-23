package com.example.graftpredict.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
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
import com.example.graftpredict.service.PdfGenerationService
import java.io.File
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportResultScreen(
    reportId: Int = 0,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sessionManager = remember(context) { SessionManager(context) }
    val apiService = remember(sessionManager) { ApiClient.create(sessionManager) }

    var reportData by remember { mutableStateOf<ReportData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isGeneratingPdf by remember { mutableStateOf(false) }

    // Color palette from HTML
    val backgroundColor = Color(0xFF101922)
    val backgroundLight = Color(0xFFF6F7F8)
    val primaryColor = Color(0xFF137FEC)

    // Fetch report data on screen init
    LaunchedEffect(reportId) {
        if (reportId > 0) {
            try {
                val response = apiService.getReportById(reportId)
                reportData = response.report
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading report: ${e.message}"
                isLoading = false
                e.printStackTrace()
            }
        }
    }

    // Handle PDF generation
    val generateAndSharePdf = {
        if (reportData != null && !isGeneratingPdf) {
            isGeneratingPdf = true
            scope.launch {
                try {
                    val pdfService = PdfGenerationService(context)
                    // Use the async method (runs on Main thread where WebView is needed)
                    val result = pdfService.generatePdfReportAsync(
                        reportData = reportData!!,
                        fileName = "GraftReport_${System.currentTimeMillis()}.pdf"
                    )

                    result.onSuccess { pdfFile ->
                        if (pdfFile.exists()) {
                            // Show success and share
                            snackbarHostState.showSnackbar(
                                message = "PDF generated successfully",
                                duration = SnackbarDuration.Short
                            )
                            sharePdfFile(context, pdfFile)
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Failed to generate PDF: File not found",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }.onFailure { e ->
                        snackbarHostState.showSnackbar(
                            message = "Error: ${e.message}",
                            duration = SnackbarDuration.Long
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    snackbarHostState.showSnackbar(
                        message = "Error: ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                } finally {
                    isGeneratingPdf = false
                }
            }
        }
    }

    // Dark mode (as per HTML)
    val isDarkMode = true

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = if (isDarkMode) backgroundColor else backgroundLight,
        bottomBar = {
            if (!isLoading && reportData != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    color = if (isDarkMode) backgroundColor.copy(alpha = 0.95f) else backgroundLight,
                    border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF324D67) else Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = generateAndSharePdf,
                            enabled = !isGeneratingPdf,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            if (isGeneratingPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Download,
                                    contentDescription = "Download",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isGeneratingPdf) "Generating PDF..." else "Download Report as PDF",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 0.015.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkMode) backgroundColor else backgroundLight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (reportData == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkMode) backgroundColor else backgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage.ifEmpty { "Report not found" },
                    color = Color.Red,
                    fontFamily = Manrope,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkMode) backgroundColor else backgroundLight)
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
                            .padding(horizontal = 16.dp),
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
                                tint = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Title
                        Text(
                            text = "Graft Size Report",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            letterSpacing = (-0.015).sp,
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                        )

                        // Share Button
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Main Content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    item {
                        // Date Chip
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = if (isDarkMode) Color(0xFF192633) else Color(0xFFE2E8F0),
                                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF324D67) else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarToday,
                                        contentDescription = "Calendar",
                                        tint = if (isDarkMode) Color(0xFF92ADC9) else Color(0xFF64748B),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Generated: ${reportData!!.submission_time}",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (isDarkMode) Color(0xFF92ADC9) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Diameter Projection Section
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDarkMode) Color(0xFF192633) else Color.White,
                                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF324D67) else Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    // Section Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Diameter Projection",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Measured in mm",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (isDarkMode) Color(0xFF92ADC9) else Color(0xFF94A3B8)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Bar Chart
                                    BarChart(
                                        isDarkMode = isDarkMode,
                                        graftD1 = reportData!!.graft_diameter_1?.toString()?.toDoubleOrNull() ?: 0.0,
                                        graftD2 = reportData!!.graft_diameter_2?.toString()?.toDoubleOrNull() ?: 0.0,
                                        hamstring = reportData!!.hamstring_autograft?.toString()?.toDoubleOrNull() ?: 0.0,
                                        quad = reportData!!.quadriceps_tendon_diameter?.toString()?.toDoubleOrNull() ?: 0.0
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Calculated Graft Details Section
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            // Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Analytics,
                                    contentDescription = "Analytics",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Calculated Graft Details",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Four Graft Cards
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    GraftCard(
                                        title = "Graft Diameter 1",
                                        value = reportData!!.graft_diameter_1?.toString() ?: "N/A",
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    GraftCard(
                                        title = "Graft Diameter 2",
                                        value = reportData!!.graft_diameter_2?.toString() ?: "N/A",
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    GraftCard(
                                        title = "Hamstring Auto",
                                        value = reportData!!.hamstring_autograft?.toString() ?: "N/A",
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    GraftCard(
                                        title = "Quad Tendon",
                                        value = reportData!!.quadriceps_tendon_diameter?.toString() ?: "N/A",
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // ST Length Table
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDarkMode) Color(0xFF151F2B) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF324D67).copy(alpha = 0.5f) else Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Row 1
                                    TableRow(
                                        label = "Minimum ST Length",
                                        value = reportData!!.minimum_st_length?.toString() ?: "N/A",
                                        unit = "mm",
                                        isDarkMode = isDarkMode,
                                        hasBorder = true,
                                        isHighlighted = false
                                    )

                                    // Row 2 (Highlighted)
                                    TableRow(
                                        label = "Predicted ST Value",
                                        value = reportData!!.predicted_st_value?.toString() ?: "N/A",
                                        unit = "mm",
                                        isDarkMode = isDarkMode,
                                        hasBorder = true,
                                        isHighlighted = true
                                    )

                                    // Row 3
                                    TableRow(
                                        label = "Gracilis Length",
                                        value = reportData!!.gracilis_length?.toString() ?: "N/A",
                                        unit = "mm",
                                        isDarkMode = isDarkMode,
                                        hasBorder = false,
                                        isHighlighted = false
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(if (isDarkMode) Color(0xFF151F2B) else Color(0xFFF1F5F9))
                        )
                    }

                    item {
                        // Patient Information Section
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            // Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "Person",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Patient Information",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Patient Info Grid
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Name (Full width)
                                InfoCard(
                                    label = "Name",
                                    value = reportData!!.name ?: "N/A",
                                    unit = "",
                                    modifier = Modifier.fillMaxWidth(),
                                    isDarkMode = isDarkMode
                                )

                                // Age and Gender Row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoCard(
                                        label = "Age",
                                        value = reportData!!.age.toString(),
                                        unit = "Years",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Gender",
                                        value = reportData!!.gender ?: "N/A",
                                        unit = "",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }

                                // Affected Side and Date Row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoCard(
                                        label = "Affected Side",
                                        value = reportData!!.affected_side ?: "N/A",
                                        unit = "",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Date",
                                        value = reportData!!.affected_date ?: "N/A",
                                        unit = "",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(if (isDarkMode) Color(0xFF151F2B) else Color(0xFFF1F5F9))
                        )
                    }

                    item {
                        // Body Metrics Section
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            // Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessibilityNew,
                                    contentDescription = "Accessibility",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Body Metrics",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Body Metrics Grid
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Row 1: Height, Weight, BMI
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoCard(
                                        label = "Height",
                                        value = reportData!!.height.toString(),
                                        unit = "cm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Weight",
                                        value = reportData!!.weight.toString(),
                                        unit = "kg",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "BMI",
                                        value = reportData!!.bmi.toString(),
                                        unit = "",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }

                                // Row 2: Leg Length, Thigh Length, Circumference
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoCard(
                                        label = "Leg Len.",
                                        value = reportData!!.leg_length.toString(),
                                        unit = "cm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Thigh Len.",
                                        value = reportData!!.thigh_length.toString(),
                                        unit = "cm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Circum.",
                                        value = reportData!!.circumfrence_thigh.toString(),
                                        unit = "cm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(if (isDarkMode) Color(0xFF151F2B) else Color(0xFFF1F5F9))
                        )
                    }

                    item {
                        // MRI Metrics Section
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            // Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MedicalServices,
                                    contentDescription = "Medical Services",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "MRI Metrics",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // MRI Metrics Grid
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Row 1
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoCard(
                                        label = "Posterior ST",
                                        value = reportData!!.posterior_st.toString(),
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Post. Gracilis",
                                        value = reportData!!.posterior_gracilis.toString(),
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }

                                // Row 2
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoCard(
                                        label = "Lateral ST",
                                        value = reportData!!.lateral_st.toString(),
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                    InfoCard(
                                        label = "Lat. Gracilis",
                                        value = reportData!!.lateral_gracilis.toString(),
                                        unit = "mm",
                                        modifier = Modifier.weight(1f),
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

// Helper function to share PDF file
private fun sharePdfFile(context: android.content.Context, file: File) {
    try {
        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "GraftPredict Report")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


@Composable
fun BarChart(
    isDarkMode: Boolean = true,
    graftD1: Double = 0.0,
    graftD2: Double = 0.0,
    hamstring: Double = 0.0,
    quad: Double = 0.0
) {
    val chartHeight = 128.dp
    val barWidth = 32.dp
    val spacing = 16.dp

    // Find maximum value to scale bars properly
    val maxValue = maxOf(graftD1, graftD2, hamstring, quad, 1.0)

    // Bar data with colors and actual values
    val bars = listOf(
        BarData(label = "Graft D1", value = graftD1, heightPercentage = (graftD1 / maxValue).toFloat(), color = Color(0xFF137FEC), highlightColor = Color(0xFF137FEC).copy(alpha = 0.8f)),
        BarData(label = "Graft D2", value = graftD2, heightPercentage = (graftD2 / maxValue).toFloat(), color = Color(0xFF8B5CF6), highlightColor = Color(0xFFA78BFA)),
        BarData(label = "Hamstring", value = hamstring, heightPercentage = (hamstring / maxValue).toFloat(), color = Color(0xFF14B8A6), highlightColor = Color(0xFF2DD4BF)),
        BarData(label = "Quad", value = quad, heightPercentage = (quad / maxValue).toFloat(), color = Color(0xFFEC4899), highlightColor = Color(0xFFF472B6)),
    )

    // Animation for bars
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight + 50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEachIndexed { index, bar ->
                var isHovered by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .width(barWidth),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Value label above the bar
                    Text(
                        text = String.format("%.2f", bar.value),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = bar.color,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Bar container with background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(chartHeight * 0.8f) // 80% of chart height for max bar
                            .background(
                                color = bar.color.copy(alpha = if (isDarkMode) 0.1f else 0.2f),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    ) {
                        // Animated bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(chartHeight * 0.8f * bar.heightPercentage * animationProgress.value)
                                .background(
                                    color = if (isHovered) bar.highlightColor else bar.color,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                                .align(Alignment.BottomStart)
                        )
                    }

                    // Bar label
                    Text(
                        text = bar.label,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = Color(0xFF92ADC9),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Horizontal grid lines
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 0.5.dp.toPx()
            val gridColor = Color(0xFF324D67).copy(alpha = 0.3f)

            // Draw 4 horizontal grid lines
            for (i in 0..3) {
                val y = size.height * (i / 3f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

data class BarData(
    val label: String,
    val value: Double = 0.0,
    val heightPercentage: Float,
    val color: Color,
    val highlightColor: Color
)

@Composable
fun GraftCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    val surfaceDark = Color(0xFF192633)
    val surfaceLight = Color.White
    val borderDark = Color(0xFF324D67)
    val borderLight = Color(0xFFE2E8F0)
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val textSecondary = Color(0xFF92ADC9)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkMode) surfaceDark else surfaceLight,
        border = BorderStroke(1.dp, if (isDarkMode) borderDark else borderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = if (isDarkMode) textSecondary else Color(0xFF64748B)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = value,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (isDarkMode) textPrimary else textPrimaryDark
                )
                Text(
                    text = unit,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF94A3B8),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TableRow(
    label: String,
    value: String,
    unit: String,
    isDarkMode: Boolean,
    hasBorder: Boolean,
    isHighlighted: Boolean
)  {
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val textSecondary = Color(0xFF92ADC9)
    val borderDark = Color(0xFF324D67)
    val borderLight = Color(0xFFE2E8F0)
    
    val backgroundColor = if (isHighlighted) {
        if (isDarkMode) Color(0xFF137FEC).copy(alpha = 0.1f) else Color(0xFFEFF6FF)
    } else {
        Color.Transparent
    }

    val textColor = if (isHighlighted) {
        if (isDarkMode) Color.White else Color(0xFF1E293B)
    } else {
        if (isDarkMode) textSecondary else Color(0xFF64748B)
    }

    val valueColor = if (isHighlighted) {
        Color(0xFF137FEC)
    } else {
        if (isDarkMode) textPrimary else Color(0xFF0F172A)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .let {
                if (hasBorder) {
                    it.border(
                        width = 0.5.dp,
                        color = if (isDarkMode) borderDark.copy(alpha = 0.5f) else borderLight,
                        shape = RoundedCornerShape(0.dp)
                    )
                } else {
                    it
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = Manrope,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                color = textColor
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = valueColor
                )
                Text(
                    text = unit,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF94A3B8),
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
)  {
    val surfaceDark = Color(0xFF192633)
    val borderDark = Color(0xFF324D67)
    val borderLight = Color(0xFFE2E8F0)
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val textSecondary = Color(0xFF92ADC9)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isDarkMode) surfaceDark.copy(alpha = 0.4f) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isDarkMode) borderDark.copy(alpha = 0.3f) else borderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isDarkMode) textSecondary else Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = value,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = if (isDarkMode) textPrimary else textPrimaryDark,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isDarkMode) textSecondary.copy(alpha = 0.7f) else Color(0xFF94A3B8),
                        modifier = Modifier
                            .padding(bottom = 1.dp, start = 2.dp)
                    )
                }
            }
        }
    }
}
