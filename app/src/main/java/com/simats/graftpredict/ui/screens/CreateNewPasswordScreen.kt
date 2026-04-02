package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.navigation.Destinations
import kotlinx.coroutines.flow.collectLatest
import com.simats.graftpredict.ui.theme.Manrope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: androidx.navigation.NavHostController, email: String) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ViewModel
    val session = remember { com.simats.graftpredict.data.local.SessionManager(context) }
    val api = remember { com.simats.graftpredict.data.api.ApiClient.create(session) }
    val repo = remember { com.simats.graftpredict.data.repository.AuthRepository(api) }
    val viewModel: com.simats.graftpredict.ui.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return com.simats.graftpredict.ui.viewmodel.AuthViewModel(repo, session) as T
        }
    })

    var resetting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.state.collectLatest { s ->
            when (s) {
                is com.simats.graftpredict.ui.viewmodel.UiState.Success -> {
                    if (resetting) {
                        resetting = false
                        // Navigate back to login and clear backstack
                        navController.navigate(Destinations.Login) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    }
                }
                is com.simats.graftpredict.ui.viewmodel.UiState.Error -> {
                    if (resetting) {
                        errorMsg = s.error
                        resetting = false
                    }
                }
                else -> {}
            }
        }
    }

    // Colors from HTML (dark theme only as per requirement)
    val backgroundColor = Color(0xFF101922) // background-dark
    val surfaceDark = Color(0xFF192633) // surface-dark
    val borderDark = Color(0xFF324D67) // border-dark
    val textSecondaryDark = Color(0xFF92ADC9) // text-secondary-dark
    val primaryBlue = Color(0xFF137FEC) // primary
    val textPrimary = Color.White
    val placeholderColor = Color(0xFF64748B) // slate-500 for dark mode placeholder

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            // Top bar - Exactly like HTML
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.Transparent)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = { navController.popBackStack() })
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Reset Password",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp,
                    color = textSecondaryDark
                )

                Box(modifier = Modifier.size(40.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp) // Changed from 32.dp to 0.dp
            ) {
                // Icon and title section - Exactly like HTML
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(top = 16.dp) // Added padding to match HTML mt-4
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(
                                8.dp,
                                RoundedCornerShape(20.dp),
                                spotColor = primaryBlue.copy(alpha = 0.1f)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                primaryBlue.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LockReset,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Create New Password",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 40.sp,
                            color = textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Your new password must be different from previous used passwords.", // Added \n for line break
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                            color = textSecondaryDark,
                            modifier = Modifier.widthIn(max = 256.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Matches HTML mt-4 on form

                // Form fields - Exactly like HTML structure
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp) // Matches HTML gap-5 (5 * 4 = 20dp)
                ) {
                    // New Password field - Custom implementation to match HTML
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Matches HTML space-y-2
                    ) {
                        Text(
                            text = "New Password",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = textPrimary
                        )

                        // Custom password field with lock icon on left
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(surfaceDark)
                                .border(
                                    1.dp,
                                    borderDark,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Lock icon on left
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = placeholderColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Password field
                                OutlinedTextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .padding(end = 44.dp), // Space for visibility toggle
                                    placeholder = {
                                        Text(
                                            "Enter new password",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = placeholderColor
                                        )
                                    },
                                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary,
                                        cursorColor = primaryBlue,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        disabledBorderColor = Color.Transparent
                                    ),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = Manrope,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                )

                                // Visibility toggle on right
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { showNewPassword = !showNewPassword },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            if (showNewPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (showNewPassword) "Hide password" else "Show password",
                                            tint = placeholderColor
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Must be at least 8 characters.",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = textSecondaryDark,
                            modifier = Modifier.padding(start = 4.dp) // Matches HTML ml-1
                        )
                    }

                    // Confirm Password field - Custom implementation to match HTML
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Matches HTML space-y-2
                    ) {
                        Text(
                            text = "Confirm Password",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = textPrimary
                        )

                        // Custom password field with lock clock icon on left
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(surfaceDark)
                                .border(
                                    1.dp,
                                    borderDark,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Lock clock icon on left
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LockClock,
                                        contentDescription = "Lock",
                                        tint = placeholderColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Password field
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .padding(end = 44.dp), // Space for visibility toggle
                                    placeholder = {
                                        Text(
                                            "Confirm new password",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = placeholderColor
                                        )
                                    },
                                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary,
                                        cursorColor = primaryBlue,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        disabledBorderColor = Color.Transparent
                                    ),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = Manrope,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                )

                                // Visibility toggle on right
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { showConfirmPassword = !showConfirmPassword },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                                            tint = placeholderColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Matches HTML mt-6 on button

                    // Reset button
                    Button(
                        onClick = {
                            errorMsg = null
                            if (newPassword.length < 8) {
                                errorMsg = "Password must be at least 8 characters"
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                errorMsg = "Passwords do not match"
                                return@Button
                            }
                            resetting = true
                            viewModel.resetPassword(email, newPassword)
                        },
                        enabled = !resetting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = primaryBlue.copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        if (resetting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                "Reset Password",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (errorMsg != null) {
                        Text(
                            text = errorMsg ?: "",
                            color = Color(0xFFEF4444),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
