package com.example.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.graftpredict.ui.navigation.Destinations
import com.example.graftpredict.ui.theme.Manrope
import com.example.graftpredict.data.local.SessionManager
import com.example.graftpredict.data.api.ApiClient
import com.example.graftpredict.data.repository.AuthRepository
import com.example.graftpredict.ui.viewmodel.AuthViewModel
import com.example.graftpredict.ui.viewmodel.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupVerifyOtpScreen(
    navController: NavHostController,
    fullName: String,
    email: String,
    password: String,
    dateOfBirth: String = "",
    gender: String = ""
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val api = remember { ApiClient.create(session) }
    val repo = remember { AuthRepository(api) }
    val viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo, session) as T
            }
        }
    )

    var otp by remember { mutableStateOf("") }
    var signingUp by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var resendInProgress by remember { mutableStateOf(false) }
    var resendMsg by remember { mutableStateOf<String?>(null) }
    var timeRemaining by remember { mutableStateOf(300) } // 5 minutes
    var canResend by remember { mutableStateOf(true) }
    var resendCountdown by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Colors from HTML (dark theme only)
    val backgroundColor = Color(0xFF101922) // background-dark
    val surfaceDark = Color(0xFF192633) // surface-dark
    val borderDark = Color(0xFF324D67) // border-dark
    val textSecondaryDark = Color(0xFF92ADC9) // text-secondary-dark
    val primaryBlue = Color(0xFF137FEC) // primary
    val textPrimary = Color.White
    val placeholderDark = Color(0xFF64748B) // slate-600 for dark mode placeholder

    // Timer for resend countdown function
    val startResendCountdown: (Int) -> Unit = { seconds: Int ->
        coroutineScope.launch {
            var countdown = seconds
            while (countdown > 0) {
                delay(1000)
                countdown--
                resendCountdown = countdown
            }
            canResend = true
            resendCountdown = 0
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.state.collectLatest { state ->
            when (state) {
                is UiState.Success -> {
                    if (signingUp) {
                        signingUp = false
                        errorMsg = null
                        // After successful signup, go back to login and clear back stack
                        navController.navigate(Destinations.Login) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    } else if (resendInProgress) {
                        resendMsg = state.message ?: "OTP resent successfully"
                        resendInProgress = false
                        canResend = false
                        resendCountdown = 60
                        startResendCountdown(60)
                    }
                }
                is UiState.Error -> {
                    if (signingUp) {
                        signingUp = false
                        errorMsg = state.error
                    } else if (resendInProgress) {
                        resendMsg = state.error
                        resendInProgress = false
                        // Extract retry seconds if available
                        val retrySeconds = extractRetrySeconds(state.error)
                        if (retrySeconds > 0) {
                            canResend = false
                            resendCountdown = retrySeconds
                            startResendCountdown(retrySeconds)
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    // Timer for OTP expiration
    LaunchedEffect(Unit) {
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
    }

    // Split full name into first and last (simple heuristic)
    val trimmedName = fullName.trim()
    val firstLast = remember(trimmedName) {
        val parts = trimmedName.split(" ").filter { it.isNotBlank() }
        when {
            parts.isEmpty() -> "" to ""
            parts.size == 1 -> parts[0] to ""
            else -> parts.first() to parts.drop(1).joinToString(" ")
        }
    }
    val firstName = firstLast.first
    val lastName = firstLast.second

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            // Top Bar - Exactly like HTML
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
                    text = "Verification",
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
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Icon Section - Exactly like HTML
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
                            color = primaryBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MarkEmailRead,
                        contentDescription = "Email",
                        tint = primaryBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Verify Your Email",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Enter the 6-digit verification code sent to your email address to complete signup.",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = textSecondaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 256.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // OTP Input Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "One-Time Password",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // OTP Input Field - using the same component as VerifyOtpScreen
                        OtpInputField(
                            otp = otp,
                            onOtpChange = { newOtp ->
                                otp = newOtp
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Timer
                        Text(
                            text = "Code expires in ${String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60)}",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = placeholderDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Verify & Sign Up Button
                    Button(
                        onClick = {
                            errorMsg = null
                            if (otp.length < 4) {
                                errorMsg = "Enter valid OTP"
                                return@Button
                            }
                            signingUp = true
                            viewModel.signupWithDetails(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password,
                                dateOfBirth = dateOfBirth,
                                gender = gender,
                                otp = otp
                            )
                        },
                        enabled = !signingUp,
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
                        if (signingUp) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "Verify & Create Account",
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

                    // Resend Section - CENTER ALIGNED like HTML
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't receive code?",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = textSecondaryDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = {
                                resendMsg = null
                                resendInProgress = true
                                viewModel.signupSendOtp(email)
                            },
                            enabled = canResend,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = primaryBlue
                            )
                        ) {
                            Text(
                                text = if (!canResend) "Resend in ${resendCountdown}s" else "Resend",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = primaryBlue
                            )
                        }
                    }

                    if (resendMsg != null) {
                        Text(
                            text = resendMsg ?: "",
                            color = if (resendMsg?.contains("resent", ignoreCase = true) == true)
                                Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Bottom Section - Exactly like HTML
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = borderDark.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(vertical = 32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Incorrect email address?",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = textSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                    TextButton(
                        onClick = {
                            // Go back to update email - we need to navigate to signup screen
                            navController.popBackStack()
                        }
                    ) {
                        Text(
                            text = "Update Email",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = primaryBlue
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to extract retry seconds from error messages.
 * Looks for patterns like "Please wait 45 seconds"
 */
private fun extractRetrySeconds(errorMsg: String?): Int {
    if (errorMsg == null) return 0
    return try {
        val regex = Regex("(\\d+)")
        val match = regex.find(errorMsg)
        match?.groupValues?.get(1)?.toIntOrNull() ?: 0
    } catch (e: Exception) {
        0
    }
}
