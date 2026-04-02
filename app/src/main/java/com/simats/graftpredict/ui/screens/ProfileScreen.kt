package com.simats.graftpredict.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.graftpredict.data.api.ApiClient
import com.simats.graftpredict.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Color definitions matching the HTML dark theme
val primaryColor = Color(0xFF137FEC)
val backgroundDark = Color(0xFF101922)
val surfaceDark = Color(0xFF192633)
val textSecondary = Color(0xFF92ADC9)

// Gray colors for dark theme
val gray100 = Color(0xFFF3F4F6)
val gray200 = Color(0xFFE5E7EB)
val gray300 = Color(0xFFD1D5DB)
val gray400 = Color(0xFF9CA3AF)
val gray500 = Color(0xFF6B7280)
val gray600 = Color(0xFF4B5563)
val gray700 = Color(0xFF374151)
val gray800 = Color(0xFF1F2937)
val gray900 = Color(0xFF111827)

// Slate colors for dark theme
val slate50 = Color(0xFFF8FAFC)
val slate100 = Color(0xFFF1F5F9)
val slate200 = Color(0xFFE2E8F0)
val slate300 = Color(0xFFCBD5E1)
val slate400 = Color(0xFF94A3B8)
val slate500 = Color(0xFF64748B)
val slate600 = Color(0xFF475569)
val slate700 = Color(0xFF334155)
val slate800 = Color(0xFF1E293B)
val slate900 = Color(0xFF0F172A)

// Blue colors for dark theme
val blue50 = Color(0xFFEFF6FF)
val blue100 = Color(0xFFDBEAFE)
val blue200 = Color(0xFFBFDBFE)
val blue300 = Color(0xFF93C5FD)
val blue400 = Color(0xFF60A5FA)
val blue500 = Color(0xFF3B82F6)
val blue600 = Color(0xFF2563EB)
val blue700 = Color(0xFF1D4ED8)
val blue800 = Color(0xFF1E40AF)
val blue900 = Color(0xFF1E3A8A)

// Purple colors for dark theme
val purple50 = Color(0xFFF5F3FF)
val purple100 = Color(0xFFEDE9FE)
val purple200 = Color(0xFFDDD6FE)
val purple300 = Color(0xFFC4B5FD)
val purple400 = Color(0xFFA78BFA)
val purple500 = Color(0xFF8B5CF6)
val purple600 = Color(0xFF7C3AED)
val purple700 = Color(0xFF6D28D9)
val purple800 = Color(0xFF5B21B6)
val purple900 = Color(0xFF4C1D95)

// Red colors for dark theme
val red50 = Color(0xFFFEF2F2)
val red100 = Color(0xFFFEE2E2)
val red200 = Color(0xFFFECACA)
val red300 = Color(0xFFFCA5A5)
val red400 = Color(0xFFF87171)
val red500 = Color(0xFFEF4444)
val red600 = Color(0xFFDC2626)
val red700 = Color(0xFFB91C1C)
val red800 = Color(0xFF991B1B)
val red900 = Color(0xFF7F1D1D)

// Orange colors for dark theme
val orange50 = Color(0xFFFFF7ED)
val orange100 = Color(0xFFFFEDD5)
val orange200 = Color(0xFFFED7AA)
val orange300 = Color(0xFFFDBA74)
val orange400 = Color(0xFFFB923C)
val orange500 = Color(0xFFF97316)
val orange600 = Color(0xFFEA580C)
val orange700 = Color(0xFFC2410C)
val orange800 = Color(0xFF9A3412)
val orange900 = Color(0xFF7C2D12)

// Indigo colors for dark theme
val indigo50 = Color(0xFFEEF2FF)
val indigo100 = Color(0xFFE0E7FF)
val indigo200 = Color(0xFFC7D2FE)
val indigo300 = Color(0xFFA5B4FC)
val indigo400 = Color(0xFF818CF8)
val indigo500 = Color(0xFF6366F1)
val indigo600 = Color(0xFF4F46E5)
val indigo700 = Color(0xFF4338CA)
val indigo800 = Color(0xFF3730A3)
val indigo900 = Color(0xFF312E81)

// Green colors for dark theme
val green50 = Color(0xFFF0FDF4)
val green100 = Color(0xFFDCFCE7)
val green200 = Color(0xFFBBF7D0)
val green300 = Color(0xFF86EFAC)
val green400 = Color(0xFF4ADE80)
val green500 = Color(0xFF22C55E)
val green600 = Color(0xFF16A34A)
val green700 = Color(0xFF15803D)
val green800 = Color(0xFF166534)
val green900 = Color(0xFF14532D)

// White and black
val white = Color(0xFFFFFFFF)
val black = Color(0xFF000000)

// Alpha variants for dark theme
val blackAlpha60 = Color(0x99000000)
val blackAlpha20 = Color(0x33000000)
val blue900Alpha20 = Color(0x331E40AF)
val blue900Alpha40 = Color(0x661E40AF)
val purple900Alpha20 = Color(0x335B21B6)
val red900Alpha10 = Color(0x1A7F1D1D)
val red900Alpha20 = Color(0x33991B1B)
val red900Alpha30 = Color(0x4C991B1B)
val orange900Alpha20 = Color(0x339A3412)
val indigo900Alpha20 = Color(0x334F46E5)
val green900Alpha40 = Color(0x6616534A)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackPressed: () -> Unit = {},
    onLogout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val apiService = remember(sessionManager) { ApiClient.create(sessionManager) }
    
    // User data states
    var fullName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var userGender by remember { mutableStateOf("") }
    var userDob by remember { mutableStateOf("") }
    var userLanguage by remember { mutableStateOf("") }
    var initials by remember { mutableStateOf("JD") }
    var isLoading by remember { mutableStateOf(true) }
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Expandable sections state
    var personalInfoExpanded by remember { mutableStateOf(true) }
    var changePasswordExpanded by remember { mutableStateOf(false) }
    var manageAccountExpanded by remember { mutableStateOf(false) }

    // Password fields
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Password change dialog states
    var showPasswordChangeSuccessDialog by remember { mutableStateOf(false) }
    var passwordChangeError by remember { mutableStateOf("") }
    var isChangingPassword by remember { mutableStateOf(false) }

    // Delete account dialog states
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var deleteCountdownSeconds by remember { mutableStateOf(5) }
    var isDeleteCountdownActive by remember { mutableStateOf(false) }
    var isDeletingAccount by remember { mutableStateOf(false) }
    var isAccountDeletedSuccessfully by remember { mutableStateOf(false) }

    // Edit Profile states
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSaveConfirmationDialog by remember { mutableStateOf(false) }

// Edit form fields
    var editFirstName by remember { mutableStateOf("") }
    var editLastName by remember { mutableStateOf("") }
    var editGender by remember { mutableStateOf("Male") }
    var editDateOfBirth by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    // Load user data on screen init
    LaunchedEffect(Unit) {
        try {
            // First, try to load from cache (SessionManager)
            val cachedFirstName = sessionManager.getFirstName() ?: ""
            val cachedLastName = sessionManager.getLastName() ?: ""
            val cachedUserId = sessionManager.getUserId() ?: ""
            val cachedEmail = sessionManager.getEmail() ?: ""
            val cachedDob = sessionManager.getDob() ?: ""
            val cachedGender = sessionManager.getGender() ?: ""
            val cachedAge = sessionManager.getAge()
            val cachedLanguage = sessionManager.getLanguage() ?: ""
            val cachedUserRole = sessionManager.getUserRole() ?: ""
            
            // Update UI with cached data
            fullName = "$cachedFirstName $cachedLastName".trim()
            userId = cachedUserId
            userEmail = cachedEmail
            userDob = cachedDob
            userGender = cachedGender
            userAge = if (cachedAge > 0) cachedAge.toString() else ""
            userLanguage = cachedLanguage
            userRole = cachedUserRole
            initials = "${cachedFirstName.firstOrNull()?.uppercaseChar() ?: 'J'}${cachedLastName.firstOrNull()?.uppercaseChar() ?: 'D'}"
            
            // Then try to fetch fresh data from API
            val token = sessionManager.getToken()
            if (token != null) {
                try {
                    val response = apiService.getUserDetails()
                    
                    if (response.user_details != null) {
                        val details = response.user_details
                        val firstName = details.first_name ?: ""
                        val lastName = details.last_name ?: ""
                        val email = details.email ?: ""
                        val id = details.id?.toString() ?: ""
                        val dob = details.dob ?: ""
                        val gender = details.gender ?: ""
                        val age = details.age ?: 0
                        val language = details.language ?: ""
                        val role = details.user ?: ""
                        
                        // Update UI with fresh data
                        fullName = "$firstName $lastName".trim()
                        userId = id
                        userEmail = email
                        userDob = dob
                        userGender = gender
                        userAge = if (age > 0) age.toString() else ""
                        userLanguage = language
                        userRole = role
                        initials = "${firstName.firstOrNull()?.uppercaseChar() ?: 'J'}${lastName.firstOrNull()?.uppercaseChar() ?: 'D'}"
                        
                        // Cache the fresh data
                        sessionManager.saveUserDetails(dob, gender, age, language, role)
                    }
                } catch (e: Exception) {
                    // API call failed, data from cache is already loaded
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // LaunchedEffect for countdown timer
    LaunchedEffect(isDeleteCountdownActive) {
        while (isDeleteCountdownActive && deleteCountdownSeconds > 0) {
            kotlinx.coroutines.delay(1000)
            deleteCountdownSeconds--
        }
        if (deleteCountdownSeconds == 0) {
            isDeleteCountdownActive = false
        }
    }

    // LaunchedEffect to handle navigation after account deletion
    LaunchedEffect(isAccountDeletedSuccessfully) {
        if (isAccountDeletedSuccessfully) {
            onNavigateToLogin()
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                // Clear session before logging out
                sessionManager.clearSession()
                onLogout()
                showLogoutDialog = false
            }
        )
    }

    if (showPasswordChangeSuccessDialog) {
        PasswordChangeSuccessDialog(
            onDismiss = {
                showPasswordChangeSuccessDialog = false
            }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountWarningDialog(
            countdownSeconds = deleteCountdownSeconds,
            isCountdownActive = isDeleteCountdownActive,
            isDeletingAccount = isDeletingAccount,
            onDismiss = {
                showDeleteAccountDialog = false
                deleteCountdownSeconds = 5
                isDeleteCountdownActive = false
            },
            onConfirm = {
                // Confirm deletion
                isDeletingAccount = true
                val coroutineScope = kotlinx.coroutines.GlobalScope
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val response = apiService.deleteAccount()
                        
                        withContext(Dispatchers.Main) {
                            if (response.error == null) {
                                // Success - Clear session and set flag to trigger navigation
                                sessionManager.clearSession()
                                showDeleteAccountDialog = false
                                deleteCountdownSeconds = 5
                                isDeleteCountdownActive = false
                                isDeletingAccount = false
                                // Set flag to trigger LaunchedEffect which will navigate
                                isAccountDeletedSuccessfully = true
                            } else {
                                // Error
                                isDeletingAccount = false
                                showDeleteAccountDialog = false
                                deleteCountdownSeconds = 5
                                isDeleteCountdownActive = false
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            isDeletingAccount = false
                            showDeleteAccountDialog = false
                            deleteCountdownSeconds = 5
                            isDeleteCountdownActive = false
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(64.dp),
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = white
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundDark.copy(alpha = 0.95f),
                    scrolledContainerColor = backgroundDark.copy(alpha = 0.95f),
                    navigationIconContentColor = white,
                    titleContentColor = white,
                    actionIconContentColor = white
                ),
                actions = {
                    IconButton(
                        onClick = { /* Handle notifications */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(surfaceDark, CircleShape)
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = white,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        containerColor = backgroundDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundDark),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Avatar
                    Box(
                        modifier = Modifier.size(112.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF60A5FA), // blue-400
                                            Color(0xFFA855F7)  // purple-600
                                        )
                                    )
                                )
                                .border(4.dp, surfaceDark, CircleShape)
                                .shadow(8.dp, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }

                        // Edit button
                        // Edit button - Change from IconButton to Box with clickable
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.BottomEnd)
                                .background(primaryColor, CircleShape)
                                .border(4.dp, backgroundDark, CircleShape)
                                .clickable {
                                    // Populate form fields with current data
                                    val nameParts = fullName.split(" ")
                                    editFirstName = nameParts.firstOrNull() ?: ""
                                    editLastName = nameParts.drop(1).joinToString(" ")
                                    editGender = userGender.ifEmpty { "Male" }
                                    editDateOfBirth = userDob.ifEmpty { "1989-04-12" }
                                    showEditProfileDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                tint = white,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = fullName.ifEmpty { "User" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = white
                    )

                    Text(
                        text = "Patient ID: #$userId",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Personal Information Card
                    ExpandableCard(
                        title = "Personal Information",
                        subtitle = "Name, DOB, Gender",
                        icon = Icons.Outlined.Person,
                        iconColor = blue400,
                        iconBackgroundColor = blue900Alpha20,
                        expanded = personalInfoExpanded,
                        onExpandedChange = { personalInfoExpanded = it }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            // Name
                            Column {
                                Text(
                                    text = "NAME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = gray500,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = fullName.ifEmpty { "Not set" },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = white
                                )
                            }

                            // Age and Gender
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "AGE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = gray500,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = userAge.ifEmpty { "Not set" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = slate300
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "GENDER",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = gray500,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = userGender.ifEmpty { "Not set" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = slate300
                                    )
                                }
                            }

                            // DOB and Role
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "DATE OF BIRTH",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = gray500,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = userDob.ifEmpty { "Not set" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = slate300
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "USER ROLE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = gray500,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(blue900Alpha40, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = userRole.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }.ifEmpty { "Patient" },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = blue300
                                        )
                                    }
                                }
                            }

                            // Email
                            Column {
                                Text(
                                    text = "EMAIL ID",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = gray500,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = userEmail.ifEmpty { "Not set" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = slate300,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Change Password Card
                    ExpandableCard(
                        title = "Change Password",
                        subtitle = "Security & Login",
                        icon = Icons.Outlined.LockReset,
                        iconColor = purple400,
                        iconBackgroundColor = purple900Alpha20,
                        expanded = changePasswordExpanded,
                        onExpandedChange = { changePasswordExpanded = it }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PasswordTextField(
                                label = "Current Password",
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                labelColor = gray500
                            )

                            PasswordTextField(
                                label = "New Password",
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                labelColor = gray500
                            )

                            PasswordTextField(
                                label = "Confirm Password",
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                labelColor = gray500
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        if (isChangingPassword) primaryColor.copy(alpha = 0.6f) else primaryColor,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = !isChangingPassword) {
                                        // Validate all fields
                                        if (currentPassword.isEmpty()) {
                                            passwordChangeError = "Current password is required"
                                            return@clickable
                                        }
                                        if (newPassword.isEmpty()) {
                                            passwordChangeError = "New password is required"
                                            return@clickable
                                        }
                                        if (confirmPassword.isEmpty()) {
                                            passwordChangeError = "Please confirm new password"
                                            return@clickable
                                        }

                                        // Validate new password rules
                                        if (!isValidNewPassword(newPassword)) {
                                            passwordChangeError = getNewPasswordValidationError(newPassword)
                                            return@clickable
                                        }

                                        // Validate passwords match
                                        if (newPassword != confirmPassword) {
                                            passwordChangeError = "New password and confirm password do not match"
                                            return@clickable
                                        }

                                        // Validate current password is different from new password
                                        if (currentPassword == newPassword) {
                                            passwordChangeError = "New password must be different from current password"
                                            return@clickable
                                        }

                                        // Clear error and proceed with password change
                                        passwordChangeError = ""
                                        isChangingPassword = true
                                        
                                        // Call backend to change password
                                        val coroutineScope = kotlinx.coroutines.GlobalScope
                                        coroutineScope.launch(Dispatchers.IO) {
                                            try {
                                                val request = com.simats.graftpredict.data.models.ChangePasswordRequest(
                                                    old_password = currentPassword,
                                                    new_password = newPassword
                                                )
                                                val response = apiService.changePassword(request)
                                                
                                                withContext(Dispatchers.Main) {
                                                    if (response.error == null && !response.message.isNullOrEmpty()) {
                                                        // Success
                                                        showPasswordChangeSuccessDialog = true
                                                        currentPassword = ""
                                                        newPassword = ""
                                                        confirmPassword = ""
                                                        changePasswordExpanded = false
                                                    } else {
                                                        passwordChangeError = response.error ?: "Failed to change password"
                                                    }
                                                    isChangingPassword = false
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    passwordChangeError = "Error: ${e.message ?: "Unknown error"}"
                                                    isChangingPassword = false
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChangingPassword) {
                                    Text(
                                        text = "Changing...",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = white
                                    )
                                } else {
                                    Text(
                                        text = "Change Password",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = white
                                    )
                                }
                            }

                            // Show error message if exists
                            if (passwordChangeError.isNotEmpty()) {
                                Text(
                                    text = passwordChangeError,
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // Manage Account Card
                    ExpandableCard(
                        title = "Manage Account",
                        subtitle = "Data & Privacy",
                        icon = Icons.Outlined.ManageAccounts,
                        iconColor = slate400,
                        iconBackgroundColor = slate800,
                        expanded = manageAccountExpanded,
                        onExpandedChange = { manageAccountExpanded = it }
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = red900Alpha10,
                                contentColor = red400
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, red900Alpha20)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Danger Zone",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = red400
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Deleting your account is permanent. All your reports and predictions will be lost.",
                                    fontSize = 12.sp,
                                    color = red400.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .background(Color(0xFF450A0A), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF991B1B), RoundedCornerShape(8.dp))
                                        .clickable {
                                            // Show delete account confirmation dialog with timer
                                            showDeleteAccountDialog = true
                                            deleteCountdownSeconds = 5
                                            isDeleteCountdownActive = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Delete Account",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = red400
                                    )
                                }
                            }
                        }
                    }

                    // Notifications Card
                    NotificationsCard(
                        notificationsEnabled = notificationsEnabled,
                        onToggle = { notificationsEnabled = !notificationsEnabled }
                    )



                    // Log Out Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(1.dp, red900Alpha30, RoundedCornerShape(16.dp))
                            .clickable { showLogoutDialog = true }
                            .background(backgroundDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Logout,
                                contentDescription = "Logout",
                                tint = red400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log Out",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = red400
                            )
                        }
                    }
                    // Edit Profile Dialog
                    if (showEditProfileDialog) {
                        EditProfileDialog(
                            firstName = editFirstName,
                            lastName = editLastName,
                            gender = editGender,
                            dateOfBirth = editDateOfBirth,
                            onFirstNameChange = { editFirstName = it },
                            onLastNameChange = { editLastName = it },
                            onGenderChange = { editGender = it },
                            onDateOfBirthChange = {
                                editDateOfBirth = it
                                showDatePicker = false
                            },
                            showDatePicker = showDatePicker,
                            onShowDatePicker = { showDatePicker = true },
                            onDismiss = { showEditProfileDialog = false },
                            onSaveChanges = {
                                showEditProfileDialog = false
                                showSaveConfirmationDialog = true
                            }
                        )
                    }

// Save Confirmation Dialog
                    if (showSaveConfirmationDialog) {
                        SaveConfirmationDialog(
                            onDismiss = { showSaveConfirmationDialog = false },
                            onConfirm = {
                                // Handle save logic here
                                showSaveConfirmationDialog = false

                                val coroutineScope = kotlinx.coroutines.GlobalScope
                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        // calculate age if dob provided
                                        var ageInt: Int? = null
                                        try {
                                            if (editDateOfBirth.isNotBlank()) {
                                                val dobLocal = java.time.LocalDate.parse(editDateOfBirth)
                                                val now = java.time.LocalDate.now()
                                                ageInt = java.time.Period.between(dobLocal, now).years
                                            }
                                        } catch (e: Exception) {
                                            ageInt = null
                                        }

                                        val req = com.simats.graftpredict.data.models.UpdateUserDetailsRequest(
                                            first_name = editFirstName,
                                            last_name = editLastName,
                                            dob = editDateOfBirth,
                                            gender = editGender,
                                            age = ageInt,
                                            language = userLanguage.ifEmpty { "English" }
                                        )

                                        val response = apiService.updateUserDetails(req)

                                        withContext(Dispatchers.Main) {
                                            if (response.error == null) {
                                                // Update local session and UI
                                                val token = sessionManager.getToken() ?: ""
                                                sessionManager.saveSession(
                                                    token = token,
                                                    userId = sessionManager.getUserId(),
                                                    firstName = editFirstName,
                                                    lastName = editLastName,
                                                    email = sessionManager.getEmail(),
                                                    userRole = sessionManager.getUserRole()
                                                )

                                                sessionManager.saveUserDetails(
                                                    editDateOfBirth,
                                                    editGender,
                                                    ageInt,
                                                    userLanguage.ifEmpty { "English" },
                                                    sessionManager.getUserRole()
                                                )

                                                fullName = "${editFirstName} ${editLastName}".trim()
                                                userDob = editDateOfBirth
                                                userGender = editGender
                                                userAge = ageInt?.toString() ?: ""
                                            } else {
                                                // handle error (could show toast)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        )
                    }

// Date Picker Dialog
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .background(primaryColor, RoundedCornerShape(8.dp))
                                        .clickable {
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                val date = Date(millis)
                                                editDateOfBirth = dateFormat.format(date)
                                                showDatePicker = false
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Select", color = white, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            },
                            dismissButton = {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .background(gray800, RoundedCornerShape(8.dp))
                                        .clickable { showDatePicker = false }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Cancel", color = gray300, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    // App Version
                    Text(
                        text = "App Version 2.4.0 (Build 102)",
                        fontSize = 12.sp,
                        color = gray600,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    iconBackgroundColor: Color,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "chevronRotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = surfaceDark,
            contentColor = white
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, gray800)
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!expanded) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconContainer(
                    icon = icon,
                    iconColor = iconColor,
                    backgroundColor = iconBackgroundColor
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = white
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = gray500
                    )
                }

                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = "Expand",
                    tint = gray600,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation)
                )
            }

            // Content
            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(blackAlpha20)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun IconContainer(
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    labelColor: Color
) {
    var isFocused by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Custom text field to match HTML design exactly
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // py-2.5 (20px) + border (2px) = approximately 48dp
                .background(
                    color = surfaceDark,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isFocused) primaryColor else gray700,
                    shape = RoundedCornerShape(12.dp)
                )
                .shadow(
                    elevation = if (isFocused) 2.dp else 0.5.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false,
                    ambientColor = if (isFocused) primaryColor.copy(alpha = 0.2f) else Color.Transparent,
                    spotColor = if (isFocused) primaryColor.copy(alpha = 0.2f) else Color.Transparent
                )
                .padding(horizontal = 12.dp, vertical = 10.dp) // px-3, py-2.5
                .focusable()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            textStyle = LocalTextStyle.current.copy(
                color = white,
                fontSize = 14.sp, // text-sm
                fontWeight = FontWeight.Normal
            ),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            cursorBrush = SolidColor(primaryColor),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = ".........",
                                color = slate500,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                    
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            tint = gray600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun NotificationsCard(
    notificationsEnabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = surfaceDark,
            contentColor = white
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, gray800)
    ) {
        Column {
            // Notifications toggle row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconContainer(
                    icon = Icons.Outlined.NotificationsActive,
                    iconColor = orange400,
                    backgroundColor = orange900Alpha20
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Notifications",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = white,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(
                            if (notificationsEnabled) green900Alpha40 else gray700,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (notificationsEnabled) "ON" else "OFF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (notificationsEnabled) green400 else gray500
                    )
                }
            }

            // Help & Support row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { /* TODO: Add help click */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconContainer(
                    icon = Icons.Outlined.Help,
                    iconColor = indigo400,
                    backgroundColor = indigo900Alpha20
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Help & Support",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = white,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = "Chevron",
                    tint = gray600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceDark
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon container
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(red900Alpha20, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = red400,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Log Out?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = white
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Are you sure you want to log out? You will need to sign in again to access your data.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(gray800, RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = gray300
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(red600, RoundedCornerShape(12.dp))
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log Out",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = white
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordChangeSuccessDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceDark
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success checkmark icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Password Changed",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = white
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your password has been successfully changed. Your new password is now active.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFF10B981), RoundedCornerShape(12.dp))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = white
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteAccountWarningDialog(
    countdownSeconds: Int,
    isCountdownActive: Boolean,
    isDeletingAccount: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isDeletingAccount,
            dismissOnClickOutside = !isDeletingAccount
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceDark
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(red900Alpha20, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = red400
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Delete Account?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = white
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This action is permanent. All your reports, predictions, and personal data will be deleted.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Countdown timer display
                if (isCountdownActive) {
                    Text(
                        text = "Confirm in: $countdownSeconds second${if (countdownSeconds != 1) "s" else ""}",
                        fontSize = 13.sp,
                        color = red400,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(gray800, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isDeletingAccount, onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = gray300
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(
                                if (isCountdownActive || isDeletingAccount) 
                                    red600.copy(alpha = 0.5f) 
                                else 
                                    red600,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(
                                enabled = !isCountdownActive && !isDeletingAccount,
                                onClick = onConfirm
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDeletingAccount) {
                            Text(
                                text = "Deleting...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        } else if (isCountdownActive) {
                            Text(
                                text = "Confirm",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = white.copy(alpha = 0.5f)
                            )
                        } else {
                            Text(
                                text = "Delete Account",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    firstName: String,
    lastName: String,
    gender: String,
    dateOfBirth: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: () -> Unit,
    onDismiss: () -> Unit,
    onSaveChanges: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceDark
            ),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, gray800)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = white
                    )

                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = slate400,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // First Name and Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FIRST NAME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = gray500,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        CustomTextField(
                            value = firstName,
                            onValueChange = { newValue ->
                                // Only allow alphabets, no spaces
                                onFirstNameChange(newValue.filter { it.isLetter() })
                            },
                            placeholder = "First Name"
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LAST NAME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = gray500,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        CustomTextField(
                            value = lastName,
                            onValueChange = { newValue ->
                                // Only allow alphabets and spaces
                                // Validate: only one space allowed, and not at start/end
                                val filtered = newValue.filter { it.isLetter() || it == ' ' }
                                
                                // Ensure no leading or trailing spaces
                                if (filtered.startsWith(' ') || filtered.endsWith(' ')) {
                                    // Do nothing, keep previous value
                                    return@CustomTextField
                                }
                                
                                // Ensure only one space between characters
                                if (filtered.contains("  ")) {
                                    // Do nothing, keep previous value
                                    return@CustomTextField
                                }
                                
                                onLastNameChange(filtered)
                            },
                            placeholder = "Last Name"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Gender
                Column {
                    Text(
                        text = "GENDER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = gray500,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Gender selector buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundDark, RoundedCornerShape(12.dp))
                            .border(1.dp, gray800, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Male", "Female", "Other").forEach { genderOption ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .background(
                                        if (gender == genderOption) primaryColor else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onGenderChange(genderOption) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = genderOption,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (gender == genderOption) white else gray500
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Date of Birth
                Column {
                    Text(
                        text = "DATE OF BIRTH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = gray500,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    EditProfileDateOfBirthTextField(
                        value = dateOfBirth,
                        onValueChange = onDateOfBirthChange
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Changes button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(primaryColor, RoundedCornerShape(12.dp))
                        .clickable(onClick = onSaveChanges)
                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = blue500.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Changes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = white
                    )
                }
            }
        }
    }
}

@Composable
fun SaveConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceDark
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, gray700)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Confirm profile updates?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = white,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(gray800, RoundedCornerShape(8.dp))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = gray400
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(primaryColor, RoundedCornerShape(8.dp))
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Confirm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = white
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(48.dp)
            .background(backgroundDark, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isFocused) primaryColor else gray700,
                shape = RoundedCornerShape(12.dp)
            )
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 2.dp,
                        color = primaryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            textStyle = LocalTextStyle.current.copy(
                color = white,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            readOnly = readOnly,
            cursorBrush = SolidColor(primaryColor),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = slate500,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

// Validation Functions
fun isValidNewPassword(password: String): Boolean {
    if (password.length < 8) return false
    if (!password.any { it.isUpperCase() }) return false
    if (!password.any { it.isLowerCase() }) return false
    if (!password.any { it.isDigit() }) return false
    if (!password.any { it in "!@#$%^&*" }) return false
    return true
}

fun getNewPasswordValidationError(password: String): String {
    return when {
        password.isEmpty() -> "Password cannot be empty"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Password must contain at least 1 uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain at least 1 lowercase letter"
        !password.any { it.isDigit() } -> "Password must contain at least 1 digit"
        !password.any { it in "!@#$%^&*" } -> "Password must contain at least 1 special character (!@#$%^&*)"
        else -> ""
    }
}

@Composable
private fun EditProfileDateOfBirthTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Calculate minimum date (14 years ago from today)
    val today = java.time.LocalDate.now()
    val minDate = today.minusYears(14)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceDark)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) primaryColor else gray800,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showDatePicker = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = {
                    Text(
                        "YYYY-MM-DD",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = slate400
                    )
                },
                interactionSource = interactionSource,
                shape = RoundedCornerShape(8.dp),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = white,
                    unfocusedTextColor = white,
                    disabledTextColor = white,
                    cursorColor = primaryColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            // Calendar icon on right
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .fillMaxHeight()
                    .clickable { showDatePicker = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Calendar",
                    tint = slate400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDateMillis ->
                if (selectedDateMillis != null) {
                    val selectedDate = java.time.Instant.ofEpochMilli(selectedDateMillis)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    
                    // Validate that selected date is at least 14 years ago
                    if (selectedDate.isBefore(minDate) || selectedDate.isEqual(minDate)) {
                        onValueChange(selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        showDatePicker = false
                    } else {
                        // Show error - date is too recent
                        android.widget.Toast.makeText(
                            context,
                            "You must be at least 14 years old",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onDismissRequest = { showDatePicker = false },
            minDate = java.time.LocalDate.now().minusYears(100).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
            maxDate = minDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
    minDate: Long,
    maxDate: Long
) {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    LaunchedEffect(Unit) {
        android.app.DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = java.util.Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                if (selectedCalendar.timeInMillis <= maxDate) {
                    onDateSelected(selectedCalendar.timeInMillis)
                }
            },
            year,
            month,
            day
        ).apply {
            datePicker.maxDate = maxDate
            datePicker.minDate = minDate
            setOnDismissListener { onDismissRequest() }
            show()
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF101922)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}