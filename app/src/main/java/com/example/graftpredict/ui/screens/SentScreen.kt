package com.example.graftpredict.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graftpredict.ui.theme.Manrope

@Composable
fun SentScreen(
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Exact colors from HTML
    val backgroundColor = Color(0xFF101922) // background-dark
    val surfaceColor = Color(0xFF192633) // surface-dark
    val borderColor = Color(0xFF324D67) // border-dark
    val textSecondary = Color(0xFF92ADC9) // text-secondary
    val primaryColor = Color(0xFF137FEC) // primary
    val textPrimary = Color.White

    // Icons colors from HTML
    val emeraldColor = Color(0xFF10B981) // emerald-500
    val amberColor = Color(0xFFF59E0B) // amber-500
    val purpleColor = Color(0xFF8B5CF6) // purple-500
    val redColor = Color(0xFFEF4444) // red-500

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for selected tab
    var selectedTab by remember { mutableStateOf(1) } // 0: All, 1: Sent, 2: Received

    // Sample data for sent reports
    val sentReports = listOf(
        SentReport(
            id = 1,
            recipientName = "Dr. Jane Smith",
            recipientType = "Doctor",
            recipientId = "12345678901",
            patientName = "John Doe",
            stValue = 255.0,
            graftValue = 8.2,
            date = "Oct 25, 2023, 10:30 AM"
        ),
        SentReport(
            id = 2,
            recipientName = "Dr. Michael Chen",
            recipientType = "Specialist",
            recipientId = "98765432109",
            patientName = "John Doe",
            stValue = 248.0,
            graftValue = 7.9,
            date = "Oct 20, 2023, 03:15 PM"
        ),
        SentReport(
            id = 3,
            recipientName = "Sarah Miller",
            recipientType = "Physiotherapist",
            recipientId = "55443322110",
            patientName = "John Doe",
            stValue = 260.0,
            graftValue = 8.5,
            date = "Sep 12, 2023, 09:00 AM"
        )
    )

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            // Bottom Navigation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                color = surfaceColor,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home Button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onHomeClick),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Home",
                            tint = textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Home",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = textSecondary
                        )
                    }

                    // Reports Button (Active)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onReportsClick),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = "Reports",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reports",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = primaryColor
                        )
                    }

                    // Profile Button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onProfileClick),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Profile",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = textSecondary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
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
                        text = "Sent Reports",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.015).sp,
                        color = textPrimary
                    )

                    // Share Button
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = surfaceColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = borderColor,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

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
                                            text = "Search recipients...",
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

            // Tabs (Segmented Control)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A3D50).copy(alpha = 0.5f), // surface-dark/50
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // All Reports Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 0) Color.White else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "All Reports",
                                    fontFamily = Manrope,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (selectedTab == 0) Color(0xFF0F172A) else textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(2.dp))

                        // Sent Reports Tab (Active)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 1) primaryColor else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sent Reports",
                                    fontFamily = Manrope,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (selectedTab == 1) Color.White else textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(2.dp))

                        // Received Reports Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 2 },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 2) Color.White else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Received Reports",
                                    fontFamily = Manrope,
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (selectedTab == 2) Color(0xFF0F172A) else textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Sent Reports List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 80.dp
                )
            ) {
                items(sentReports) { report ->
                    SentReportCard(
                        report = report,
                        onDeleteClick = {
                            // Handle delete
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SentReportCard(
    report: SentReport,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit = {}
) {
    // Exact colors from HTML
    val surfaceColor = Color(0xFF192633) // surface-dark
    val borderColor = Color(0xFF324D67) // border-dark
    val textSecondary = Color(0xFF92ADC9) // text-secondary
    val textPrimary = Color.White
    val primaryColor = Color(0xFF137FEC) // primary
    val emeraldColor = Color(0xFF10B981) // emerald-500
    val amberColor = Color(0xFFF59E0B) // amber-500
    val purpleColor = Color(0xFF8B5CF6) // purple-500
    val backgroundColor = Color(0xFF101922) // background-dark

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Recipient Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Recipient Info with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Recipient",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Column {
                        Text(
                            text = report.recipientName,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textPrimary,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "${report.recipientType} • ID: ${report.recipientId}",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = textSecondary,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Delete Button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = borderColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Metrics Grid (3 columns)
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = backgroundColor.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Patient Column
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "PATIENT",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = textSecondary.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Patient",
                                tint = emeraldColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = report.patientName,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary,
                                maxLines = 1
                            )
                        }
                    }

                    // ST Column
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "ST",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = textSecondary.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Analytics,
                                contentDescription = "ST",
                                tint = amberColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${report.stValue} mm",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary
                            )
                        }
                    }

                    // Graft Column
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "GRAFT",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = textSecondary.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Straighten,
                                contentDescription = "Graft",
                                tint = purpleColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${report.graftValue} mm",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary
                            )
                        }
                    }
                }
            }

            // Timestamp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = report.date,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = textSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

data class SentReport(
    val id: Int,
    val recipientName: String,
    val recipientType: String,
    val recipientId: String,
    val patientName: String,
    val stValue: Double,
    val graftValue: Double,
    val date: String,
    val reportId: Int = 0,
    val fromEmail: String = "",
    val toEmail: String = ""
)

// Preview for testing
@Composable
fun SentScreenPreview() {
    SentScreen(
        onBackClick = {},
        onShareClick = {},
        onHomeClick = {},
        onReportsClick = {},
        onProfileClick = {}
    )
}
