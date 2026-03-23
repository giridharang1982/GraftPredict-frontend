package com.example.graftpredict.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.graftpredict.R
import com.example.graftpredict.data.api.ApiClient
import com.example.graftpredict.data.api.ApiService
import com.example.graftpredict.data.local.SessionManager
import com.example.graftpredict.data.models.Doctor
import com.example.graftpredict.data.models.DoctorActionRequest
import com.example.graftpredict.ui.theme.Manrope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


// Helper function to format date of birth
private fun formatDateOfBirth(dateString: String): String {
    return try {
        // Parse the incoming date format: "Sat, 10 Jul 1999 00:00:00 GMT"
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        val date = inputFormat.parse(dateString)
        
        // Format to desired output: "10 Jan 1999"
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

// Helper function to fetch doctors from API
private fun fetchDoctors(
    api: ApiService,
    callback: (List<Doctor>, String?) -> Unit
) {
    GlobalScope.launch {
        try {
            val response = api.getDoctors()
            if (response.error == null && response.doctors != null) {
                callback(response.doctors, null)
            } else {
                callback(emptyList(), response.error ?: "Unknown error")
            }
        } catch (e: Exception) {
            callback(emptyList(), e.message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: androidx.navigation.NavHostController? = null,
    onAddDoctorClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val api = remember { ApiClient.create(sessionManager) as ApiService }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedDoctorId by remember { mutableStateOf<String?>(null) }
    var menuOpenDoctorId by remember { mutableStateOf<String?>(null) }
    var showAddDoctor by remember { mutableStateOf(false) }

    // Fetch doctors on composition
    LaunchedEffect(Unit) {
        fetchDoctors(api) { updatedDoctors, error ->
            if (error != null) {
                errorMsg = error
            } else {
                doctors = updatedDoctors
            }
            isLoading = false
        }
    }

    // Show logout confirmation dialog
    if (showLogoutDialog) {
        AdminLogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                sessionManager.clearSession()
                onLogout()
                showLogoutDialog = false
            }
        )
    }

    // Colors from HTML
    val backgroundColor = Color(0xFF101922)
    val cardDark = Color(0xFF1A2530)
    val cardDarkHighlight = Color(0xFF23303D)
    val primaryBlue = Color(0xFF137FEC)
    val textPrimary = Color.White
    val textSecondary = Color(0xFF94A3B8)
    val borderColor = Color(0xFF1E293B)
    val greenActive = Color(0xFF10B981)
    val redRestricted = Color(0xFFEF4444)
    val searchBg = cardDark

    // Show add doctor screen instead of home screen
    if (showAddDoctor) {
        AdminAddDoctorScreen(
            onBackPressed = {
                showAddDoctor = false
                // Refresh doctors after adding
                isLoading = true
                fetchDoctors(api) { updatedDoctors, error ->
                    if (error == null) {
                        doctors = updatedDoctors
                    }
                    isLoading = false
                }
            },
            onHomeClick = { showAddDoctor = false },
            onLogoutClick = { showLogoutDialog = true }
        )
        return
    }

    Scaffold(
        containerColor = backgroundColor,
        contentColor = textPrimary,
        bottomBar = {
            BottomNavigationBar(
                backgroundColor = cardDark,
                borderColor = borderColor,
                primaryBlue = primaryBlue,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onAddDoctorClick = { showAddDoctor = true },
                onLogoutClick = { showLogoutDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage Doctors",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = textPrimary,
                        letterSpacing = (-0.025).sp
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, primaryBlue.copy(alpha = 0.2f), CircleShape)
                            .background(Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data("https://lh3.googleusercontent.com/aida-public/AB6AXuBHGYRxez6LGFxWxDxFX6FeJPh3ZrpzRW6JItT2T0DyFiSasWtdBn3kEbyU2seLT9Fn_V579TwUxgPpgagfhyxr38wpuTqktQgVeH3Dhtq-rb-D58FlPMOBhNeVlDkT13kcnoNlsuVc8DwWRWy7-xto-W6kHWmEp5XGFsNiVKwN-eEL5qCGNd6I2YTebJiORy_J-LT_Iz68xr3uump_rJXvuXHPG9hqPjGoeWaZw-9LusBrs62JG7KukeB5NIdN4WxhB5JDRsfckalG")
                                    .build()
                            ),
                            contentDescription = "Admin profile avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Search doctors by name or ID...",
                                fontFamily = Manrope,
                                fontSize = 14.sp,
                                color = textSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = searchBg,
                            unfocusedContainerColor = searchBg,
                            disabledContainerColor = searchBg,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            cursorColor = primaryBlue,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = Manrope,
                            fontSize = 14.sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {}
                        )
                    )
                }
            }

            // Doctors List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            } else if (errorMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $errorMsg",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                errorMsg = null
                                isLoading = true
                                fetchDoctors(api) { updatedDoctors, error ->
                                    if (error != null) {
                                        errorMsg = error
                                    } else {
                                        doctors = updatedDoctors
                                    }
                                    isLoading = false
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                val filteredDoctors = doctors.filter {
                    searchQuery.isEmpty() || 
                    it.first_name.contains(searchQuery, ignoreCase = true) ||
                    it.last_name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    items(filteredDoctors, key = { it.id }) { doctor ->
                        val isExpanded = expandedDoctorId == doctor.id
                        val isMenuOpen = menuOpenDoctorId == doctor.id
                        val doctorStatus = if (doctor.email.endsWith("RESTRICTED")) "RESTRICTED" else "ACTIVE"
                        
                        AdminDoctorCard(
                            doctor = doctor,
                            status = doctorStatus,
                            isExpanded = isExpanded,
                            isMenuOpen = isMenuOpen,
                            onExpandToggle = {
                                expandedDoctorId = if (isExpanded) null else doctor.id
                            },
                            onMenuToggle = {
                                menuOpenDoctorId = if (isMenuOpen) null else doctor.id
                            },
                            onRestrict = {
                                GlobalScope.launch {
                                    try {
                                        api.restrictDoctor(DoctorActionRequest(doctor.id))
                                        menuOpenDoctorId = null
                                        isLoading = true
                                        fetchDoctors(api) { updatedDoctors, error ->
                                            if (error == null) {
                                                doctors = updatedDoctors
                                            }
                                            isLoading = false
                                        }
                                    } catch (e: Exception) {
                                        errorMsg = e.message
                                    }
                                }
                            },
                            onActivate = {
                                GlobalScope.launch {
                                    try {
                                        api.activateDoctor(DoctorActionRequest(doctor.id))
                                        menuOpenDoctorId = null
                                        isLoading = true
                                        fetchDoctors(api) { updatedDoctors, error ->
                                            if (error == null) {
                                                doctors = updatedDoctors
                                            }
                                            isLoading = false
                                        }
                                    } catch (e: Exception) {
                                        errorMsg = e.message
                                    }
                                }
                            },
                            cardDark = cardDark,
                            cardDarkHighlight = cardDarkHighlight,
                            primaryBlue = primaryBlue,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            borderColor = borderColor,
                            greenActive = greenActive,
                            redRestricted = redRestricted
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// First, update the AdminDoctorCard composable to match the HTML design

@Composable
private fun AdminDoctorCard(
    doctor: Doctor,
    status: String,
    isExpanded: Boolean,
    isMenuOpen: Boolean,
    onExpandToggle: () -> Unit,
    onMenuToggle: () -> Unit,
    onRestrict: () -> Unit,
    onActivate: () -> Unit,
    cardDark: Color,
    cardDarkHighlight: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    greenActive: Color,
    redRestricted: Color
) {
    // Generate initials from doctor name
    val initials = "${doctor.first_name.firstOrNull()?.uppercaseChar() ?: 'D'}${doctor.last_name.firstOrNull()?.uppercaseChar() ?: 'O'}"
    // Use a deterministic color based on doctor ID
    val colors = listOf(
        Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899),
        Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF06B6D4),
        Color(0xFF6366F1), Color(0xFFF97316)
    )
    val avatarColor = colors[(doctor.id?.hashCode() ?: 0).mod(colors.size)]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(cardDark)
    ) {
        // Header - Clickable label for expand/collapse
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandToggle() }
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar with initials
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                // Avatar circle with initials
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(avatarColor)
                        .border(1.dp, borderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                // Status indicator dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (status == "ACTIVE") greenActive else redRestricted)
                        .border(2.dp, cardDark, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Doctor info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Dr. ${doctor.first_name} ${doctor.last_name}",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ID: ${doctor.id}",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Status badge
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (status == "ACTIVE") Color(0xFF065F46).copy(alpha = 0.1f)
                            else Color(0xFF7F1D1D).copy(alpha = 0.1f)
                        )
                ) {
                    Text(
                        text = status,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (status == "ACTIVE") greenActive else redRestricted,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Menu and expand icons
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Expand chevron
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = textSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (isExpanded) 180f else 0f)
                )

                // Menu button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(cardDarkHighlight)
                        .clickable {
                            onMenuToggle()
                            // Close expansion if menu is toggled
                            if (isExpanded) {
                                onExpandToggle()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "Menu",
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Menu dropdown
        if (isMenuOpen) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardDarkHighlight)
                    .padding(vertical = 8.dp)
            ) {
                if (status == "ACTIVE") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRestrict() }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = "Restrict",
                            tint = redRestricted,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Restrict Account",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onActivate() }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Activate",
                            tint = greenActive,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Activate Account",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                    }
                }
            }
        }

        // Expanded content (2-column grid like HTML)
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardDarkHighlight)
                    .padding(16.dp)
            ) {
                // NAME section (full width)
                DetailGridItem(
                    label = "NAME",
                    value = "${doctor.first_name} ${doctor.last_name}",
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    spanFullWidth = true,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Grid for other details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Column 1
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailGridItem(
                            label = "AGE",
                            value = doctor.age?.toString() ?: "N/A",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                        DetailGridItem(
                            label = "DATE OF BIRTH",
                            value = formatDateOfBirth(doctor.dob ?: "N/A"),
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }

                    // Column 2
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailGridItem(
                            label = "GENDER",
                            value = doctor.gender ?: "N/A",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                        DetailGridItem(
                            label = "USER ROLE",
                            value = "Doctor",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isRole = true
                        )
                    }
                }

                // EMAIL ID section (full width)
                Spacer(modifier = Modifier.height(16.dp))
                DetailGridItem(
                    label = "EMAIL ID",
                    value = doctor.email.replace("RESTRICTED", "").trim(),
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    spanFullWidth = true
                )
            }
        }
    }
}

@Composable
private fun DetailGridItem(
    label: String,
    value: String,
    textPrimary: Color,
    textSecondary: Color,
    spanFullWidth: Boolean = false,
    isRole: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = if (spanFullWidth) Modifier.fillMaxWidth() else modifier
    ) {
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = textSecondary,
            letterSpacing = 1.sp
        )

        if (isRole) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF137FEC).copy(alpha = 0.2f))
            ) {
                Text(
                    text = value,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF137FEC),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        } else {
            Text(
                text = value,
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Update the BottomNavigationBar to match HTML design exactly
@Composable
private fun BottomNavigationBar(
    backgroundColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    onAddDoctorClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(backgroundColor)
            .border(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home button (active)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = primaryBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Home",

                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = primaryBlue
                )
            }

            // Add Doctor button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddDoctorClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Add Doctor",
                    tint = textSecondary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add Doctor",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = textSecondary
                )
            }

            // Logout button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLogoutClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = textSecondary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Logout",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = textSecondary
                )
            }
        }
    }
}
@Composable
private fun AdminLogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .wrapContentWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2530))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Logout",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Are you sure you want to logout?",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3F5F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", fontFamily = Manrope, fontSize = 14.sp)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Logout", fontFamily = Manrope, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
