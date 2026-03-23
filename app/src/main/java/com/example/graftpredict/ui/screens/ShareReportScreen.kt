package com.example.graftpredict.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.graftpredict.ui.theme.Manrope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.graftpredict.data.api.ApiClient
import com.example.graftpredict.data.local.SessionManager
import com.example.graftpredict.data.models.ShareReportRequest
import com.example.graftpredict.data.models.SharedUser
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun ShareReportScreen(
    reportId: Int = 0,
    onBackClick: () -> Unit = {},
    onShareClick: (userId: String) -> Unit = {},
    onRevokeAccessClick: (userId: String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember(context) { SessionManager(context) }
    val apiService = remember(sessionManager) { ApiClient.create(sessionManager) }

    var searchText by remember { mutableStateOf("") }
    var searchedUser by remember { mutableStateOf<SearchedUser?>(null) }
    var sentReportUsers by remember { mutableStateOf<List<SentReportUser>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var isSharing by remember { mutableStateOf(false) }
    var shareMessage by remember { mutableStateOf<String?>(null) }
    var sharedUsers by remember { mutableStateOf<List<SharedUser>>(emptyList()) }
    var isLoadingSharedUsers by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<SharedUser?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Fetch shared users on screen load
    LaunchedEffect(reportId) {
        isLoadingSharedUsers = true
        try {
            val currentUserId = sessionManager.getUserId()?.toLongOrNull()
            if (currentUserId != null) {
                val response = apiService.getSharedUsersForReport(reportId, currentUserId)
                sharedUsers = response.shared_users ?: emptyList()
            }
        } catch (e: Exception) {
            println("Error loading shared users: ${e.message}")
            sharedUsers = emptyList()
        } finally {
            isLoadingSharedUsers = false
        }
    }

    // Color palette from HTML
    val backgroundColor = Color(0xFF101922)
    val backgroundLight = Color(0xFFF6F7F8)
    val surfaceDark = Color(0xFF1C2936)
    val surfaceDarkHover = Color(0xFF233342)
    val textSecondary = Color(0xFF92ADC9)
    val primaryColor = Color(0xFF137FEC)
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val borderDark = Color(0xFF233648)
    val borderHoverDark = Color(0xFF344D66)
    val borderLight = Color(0xFFE2E8F0)
    val surfaceLight = Color.White
    val surfaceLightHover = Color(0xFFF8FAFC)

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
                color = if (isDarkMode) backgroundColor else backgroundLight,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                        text = "Share Report",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.015).sp,
                        color = if (isDarkMode) textPrimary else textPrimaryDark,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Search Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkMode) surfaceDark else surfaceLight,
                    border = BorderStroke(1.dp, if (isDarkMode) borderDark else borderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = if (isDarkMode) textSecondary else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Text Field
                        TextField(
                            value = searchText,
                            onValueChange = { newValue ->
                                // Only allow numeric input and limit to 11 digits
                                if (newValue.all { it.isDigit() } && newValue.length <= 11) {
                                    searchText = newValue
                                    // When 11 digits are entered, search automatically
                                    if (newValue.length == 11) {
                                        // Search via API
                                        isSearching = true
                                        searchError = null
                                        scope.launch {
                                            try {
                                                val userId = newValue.toLong()
                                                val response = apiService.getUserById(userId)
                                                
                                                if (response.user_details != null) {
                                                    val userDetails = response.user_details
                                                    searchedUser = SearchedUser(
                                                        id = userId,
                                                        firstName = userDetails.first_name ?: "Unknown",
                                                        lastName = userDetails.last_name ?: "",
                                                        userRole = userDetails.user ?: "Patient"
                                                    )
                                                    searchError = null
                                                } else {
                                                    searchedUser = null
                                                    searchError = response.error ?: "User not found"
                                                }
                                            } catch (e: Exception) {
                                                searchedUser = null
                                                searchError = "Error searching user: ${e.message}"
                                                e.printStackTrace()
                                            } finally {
                                                isSearching = false
                                            }
                                        }
                                    } else {
                                        searchedUser = null
                                        searchError = null
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = searchText.length < 11, // Freeze search bar after 11 digits
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = if (isDarkMode) textPrimary else textPrimaryDark,
                                unfocusedTextColor = if (isDarkMode) textPrimary else textPrimaryDark,
                                disabledTextColor = if (isDarkMode) textSecondary else Color(0xFF94A3B8),
                                cursorColor = primaryColor,
                            ),
                            placeholder = {
                                Text(
                                    text = "Search by User ID (11 digits)",
                                    fontFamily = Manrope,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = if (isDarkMode) textSecondary else Color(0xFF94A3B8)
                                )
                            },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = Manrope,
                                fontSize = 16.sp
                            ),
                            trailingIcon = {
                                if (searchText.length == 11) {
                                    IconButton(
                                        onClick = {
                                            searchText = ""
                                            searchedUser = null
                                            searchError = null
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Clear",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Search Result Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                // Only show if search is active
                if (searchText.length == 11) {
                    // Section Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonSearch,
                            contentDescription = "Person Search",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "SEARCH RESULT",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = if (isDarkMode) textSecondary else Color(0xFF64748B)
                        )
                    }

                    // Display loading state
                    if (isSearching) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = primaryColor,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Searching...",
                                fontFamily = Manrope,
                                color = if (isDarkMode) textSecondary else Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Display error message
                    if (searchError != null && !isSearching) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF7F1D1D).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = searchError!!,
                                    fontFamily = Manrope,
                                    fontSize = 14.sp,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }

                    // Display search result if user found
                    if (searchedUser != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDarkMode) surfaceDark else surfaceLight,
                            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // User Info with Avatar
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Avatar with Online Indicator
                                        Box(
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            // Avatar Image or Initials
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isDarkMode) Color(0xFF374151) else Color(0xFFE2E8F0)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${searchedUser!!.firstName.first()}${searchedUser!!.lastName.first()}".uppercase(),
                                                    fontFamily = Manrope,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    color = primaryColor
                                                )
                                            }

                                            // Online Indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF10B981))
                                                    .border(2.dp, if (isDarkMode) surfaceDark else surfaceLight, CircleShape)
                                                    .align(Alignment.BottomEnd)
                                            )
                                        }

                                        // User Details
                                        Column(
                                            modifier = Modifier.fillMaxWidth(0.6f)
                                        ) {
                                            Text(
                                                text = "${searchedUser!!.firstName} ${searchedUser!!.lastName}",
                                                fontFamily = Manrope,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (isDarkMode) textPrimary else textPrimaryDark,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "ID: ${searchedUser!!.id} • ${searchedUser!!.userRole}",
                                                fontFamily = Manrope,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                color = if (isDarkMode) textSecondary else Color(0xFF64748B),
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    // Share Button
                                    Button(
                                        onClick = {
                                            if (searchedUser != null && !isSharing) {
                                                isSharing = true
                                                shareMessage = null
                                                scope.launch {
                                                    try {
                                                        val currentUserId = sessionManager.getUserId()?.toLongOrNull() 
                                                            ?: throw Exception("User ID not available")
                                                        val response = apiService.shareReport(
                                                            ShareReportRequest(
                                                                from_user_id = currentUserId,
                                                                to_user_id = searchedUser!!.id,
                                                                report_id = reportId
                                                            )
                                                        )
                                                        shareMessage = response.message ?: "Report shared successfully"
                                                    } catch (e: Exception) {
                                                        shareMessage = when {
                                                            e.message?.contains("Cannot share") == true -> "Cannot share report with yourself"
                                                            e.message?.contains("already shared") == true -> "Report already shared with this user"
                                                            else -> "Error sharing report: ${e.message}"
                                                        }
                                                    } finally {
                                                        isSharing = false
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isSharing,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primaryColor,
                                            contentColor = Color.White,
                                            disabledContainerColor = Color(0xFF6B7280)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 8.dp,
                                            pressedElevation = 4.dp
                                        ),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        if (isSharing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Share",
                                                fontFamily = Manrope,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Display share message
                    if (shareMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (shareMessage!!.contains("Error") || shareMessage!!.contains("Cannot") || shareMessage!!.contains("already"))
                                Color(0xFF7F1D1D).copy(alpha = 0.2f)
                            else
                                Color(0xFF065F46).copy(alpha = 0.2f),
                            border = BorderStroke(
                                1.dp,
                                if (shareMessage!!.contains("Error") || shareMessage!!.contains("Cannot") || shareMessage!!.contains("already"))
                                    Color(0xFFDC2626).copy(alpha = 0.5f)
                                else
                                    Color(0xFF10B981).copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (shareMessage!!.contains("Error") || shareMessage!!.contains("Cannot") || shareMessage!!.contains("already"))
                                        Icons.Outlined.ErrorOutline
                                    else
                                        Icons.Outlined.CheckCircleOutline,
                                    contentDescription = "Message",
                                    tint = if (shareMessage!!.contains("Error") || shareMessage!!.contains("Cannot") || shareMessage!!.contains("already"))
                                        Color(0xFFDC2626)
                                    else
                                        Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = shareMessage!!,
                                    fontFamily = Manrope,
                                    fontSize = 14.sp,
                                    color = if (shareMessage!!.contains("Error") || shareMessage!!.contains("Cannot") || shareMessage!!.contains("already"))
                                        Color(0xFFDC2626)
                                    else
                                        Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            )

            // Currently Shared List Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp).paddingFromBaseline(top = 24.dp)
            ) {
                // Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Group,
                            contentDescription = "Group",
                            tint = if (isDarkMode) textSecondary else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "CURRENTLY SHARED WITH",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = if (isDarkMode) textSecondary else Color(0xFF64748B)
                        )
                    }

                    // Count Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDarkMode) surfaceDark else Color(0xFFF1F5F9),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        Text(
                            text = "${sharedUsers.size} Users",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isDarkMode) textSecondary else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Shared Users List - Dynamic cards
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isLoadingSharedUsers) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterHorizontally),
                            color = primaryColor
                        )
                    } else if (sharedUsers.isEmpty()) {
                        Text(
                            text = "No reports shared yet",
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            color = if (isDarkMode) textSecondary else Color(0xFF64748B),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        sharedUsers.forEach { user ->
                            val fullName = "${user.first_name} ${user.last_name}".trim()
                            val initials = (user.first_name?.firstOrNull()?.uppercase() ?: "") +
                                    (user.last_name?.firstOrNull()?.uppercase() ?: "")
                            
                            SharedUserItem(
                                name = fullName,
                                info = "ID: ${user.id} • ${user.user ?: "user"}",
                                avatarUrl = null,
                                initials = initials,
                                hasAvatar = false,
                                avatarBackground = Color(0xFF374151),
                                avatarTextColor = Color.White,
                                isDarkMode = isDarkMode,
                                onRevokeClick = {
                                    userToDelete = user
                                    showDeleteConfirmation = true
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Info Note
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = if (isDarkMode) textSecondary else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Sharing grants read-only access to this patient report. You can revoke access at any time using the delete icon. All actions are logged for HIPAA compliance.",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = if (isDarkMode) textSecondary else Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Remove Share",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove access for ${userToDelete?.first_name} ${userToDelete?.last_name}? This action cannot be undone.",
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    color = if (isDarkMode) textSecondary else Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isDeleting = true
                            try {
                                val currentUserId = sessionManager.getUserId()?.toLongOrNull()
                                if (currentUserId != null && userToDelete != null) {
                                    val response = apiService.deleteSharedReport(
                                        userToDelete!!.sno ?: 0
                                    )
                                    if (response.message?.contains("deleted") == true) {
                                        sharedUsers = sharedUsers.filter { it.sno != userToDelete?.sno }
                                        shareMessage = "Access removed successfully"
                                    } else {
                                        shareMessage = "Error removing access"
                                    }
                                }
                            } catch (e: Exception) {
                                shareMessage = "Error: ${e.message}"
                                println("Delete error: ${e.message}")
                            } finally {
                                isDeleting = false
                                showDeleteConfirmation = false
                                userToDelete = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    ),
                    enabled = !isDeleting
                ) {
                    Text(
                        text = if (isDeleting) "Deleting..." else "Delete",
                        fontFamily = Manrope,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF374151)
                    ),
                    enabled = !isDeleting
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = Manrope,
                        color = Color.White
                    )
                }
            },
            containerColor = if (isDarkMode) surfaceDark else surfaceLight,
            textContentColor = if (isDarkMode) textPrimary else textPrimaryDark
        )
    }
}

@Composable
fun SharedUserItem(
    name: String,
    info: String,
    avatarUrl: String? = null,
    initials: String = "",
    hasAvatar: Boolean = true,
    avatarBackground: Color = Color(0xFF374151),
    avatarTextColor: Color = Color.White,
    isDarkMode: Boolean,
    onRevokeClick: () -> Unit
) {
    val surfaceDark = Color(0xFF1C2936)
    val surfaceLight = Color.White
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val textSecondary = Color(0xFF92ADC9)
    
    val backgroundColor = if (isDarkMode) surfaceDark else surfaceLight
    val borderColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val hoverBorderColor = if (isDarkMode) Color(0xFF374151) else Color(0xFFCBD5E1)

    var isHovered by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, if (isHovered) hoverBorderColor else Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info with Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasAvatar && avatarUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatarUrl)
                                .build(),
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(avatarBackground)
                        )
                    } else {
                        // Initials avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) Color(0xFF4C1D95).copy(alpha = 0.3f) else Color(0xFFE0E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isDarkMode) Color(0xFFA78BFA) else Color(0xFF4F46E5)
                            )
                        }
                    }
                }

                // User Details
                Column(
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = name,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDarkMode) textPrimary else textPrimaryDark,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = info,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isDarkMode) textSecondary else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            // Delete Button
            IconButton(
                onClick = onRevokeClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Revoke Access",
                    tint = if (isHovered) Color(0xFFEF4444) else if (isDarkMode) textSecondary else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Data classes for dynamic card display
data class SearchedUser(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val userRole: String
)

data class SentReportUser(
    val id: Long,
    val name: String,
    val userRole: String,
    val initials: String,
    val avatarColor: Color
)
