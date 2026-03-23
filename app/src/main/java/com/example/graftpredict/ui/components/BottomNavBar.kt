package com.example.graftpredict.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graftpredict.ui.theme.Manrope

@Composable
fun BottomNavBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    isDarkMode: Boolean = true
) {
    // Color palette from HomeScreen
    val backgroundColor = Color(0xFF101922)
    val backgroundLight = Color(0xFFF6F7F8)
    val borderColor = Color(0xFF1E293B)
    val borderLight = Color(0xFFE2E8F0)
    val primaryBlue = Color(0xFF137FEC)
    val iconGray = Color(0xFF64748B)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        color = (if (isDarkMode) backgroundColor else backgroundLight).copy(alpha = 0.9f),
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, if (isDarkMode) borderColor else borderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Home Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onHomeClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (currentRoute == "home") primaryBlue else iconGray,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Home",
                    fontFamily = Manrope,
                    fontWeight = if (currentRoute == "home") FontWeight.Bold else FontWeight.Medium,
                    fontSize = 10.sp,
                    color = if (currentRoute == "home") primaryBlue else iconGray
                )
            }

            // Report Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onReportClick() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Summarize,
                    contentDescription = "Report",
                    tint = if (currentRoute == "manage_report" || currentRoute == "report") primaryBlue else iconGray,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Report",
                    fontFamily = Manrope,
                    fontWeight = if (currentRoute == "manage_report" || currentRoute == "report") FontWeight.Bold else FontWeight.Medium,
                    fontSize = 10.sp,
                    color = if (currentRoute == "manage_report" || currentRoute == "report") primaryBlue else iconGray
                )
            }

            // Profile Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    tint = if (currentRoute == "profile") primaryBlue else iconGray,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Profile",
                    fontFamily = Manrope,
                    fontWeight = if (currentRoute == "profile") FontWeight.Bold else FontWeight.Medium,
                    fontSize = 10.sp,
                    color = if (currentRoute == "profile") primaryBlue else iconGray
                )
            }
        }
    }
}
