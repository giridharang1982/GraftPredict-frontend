package com.example.graftpredict.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graftpredict.ui.theme.Manrope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.graftpredict.data.api.ApiClient
import com.example.graftpredict.data.local.SessionManager
import com.example.graftpredict.data.repository.AuthRepository
import com.example.graftpredict.ui.viewmodel.AuthViewModel
import com.example.graftpredict.ui.viewmodel.UiState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackPressed: () -> Unit = {},
    onSendOtp: (String) -> Unit = {},
    onLoginPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val api = remember { ApiClient.create(session) }
    val repo = remember { AuthRepository(api) }
    val viewModel: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(repo, session) as T
        }
    })
    // Color palette from Figma/Tailwind design
    val backgroundColor = Color(0xFF101922)
    val surfaceColor = Color(0xFF192633)
    val borderColor = Color(0xFF324D67)
    val primaryBlue = Color(0xFF137FEC)
    val textPrimary = Color.White
    val textSecondary = Color(0xFF92ADC9)
    val textTertiary = Color(0xFF64748B)
    val textQuaternary = Color(0xFF94A3B8)

    // State variables
    var email by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var infoMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.state.collectLatest { state ->
            when (state) {
                is UiState.Success -> {
                    if (isSending) {
                        isSending = false
                        infoMsg = state.message ?: "OTP sent to your email"
                        errorMsg = null
                        onSendOtp(email.trim())
                    }
                }
                is UiState.Error -> {
                    if (isSending) {
                        isSending = false
                        errorMsg = state.error
                        infoMsg = null
                    }
                }
                else -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar - Same pattern as LoginScreen
            ForgotPasswordTopBar(
                onBackPressed = onBackPressed,
                backgroundColor = backgroundColor,
                textSecondary = textSecondary
            )

            // Main Content Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 100.dp), // Space for bottom CTA
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Icon - Same pattern as LoginScreen
                ForgotPasswordLogoIcon(primaryBlue = primaryBlue)

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Forgot Password",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    letterSpacing = (-0.75).sp,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                Text(
                    text = "Enter your email address to receive\nOTP for password reset.",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Email Field - Using same pattern as LoginScreen
                ForgotPasswordEmailField(
                    value = email,
                    onValueChange = { email = it },
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Send OTP Button
                Button(
                    onClick = {
                        errorMsg = null
                        infoMsg = null
                        val trimmedEmail = email.trim()
                        if (trimmedEmail.isEmpty()) {
                            errorMsg = "Email is required"
                            return@Button
                        }
                        isSending = true
                        viewModel.sendForgotOtp(trimmedEmail)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Text(
                        text = if (isSending) "Sending OTP..." else "Send OTP",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color.White
                    )
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = Color(0xFFFF6B6B),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (infoMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = infoMsg ?: "",
                        color = primaryBlue,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Bottom CTA - Fixed at bottom (same pattern as LoginScreen)
        BottomLoginPrompt(
            onLoginPressed = onLoginPressed,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            textSecondary = textSecondary,
            primaryBlue = primaryBlue,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ForgotPasswordTopBar(
    onBackPressed: () -> Unit,
    backgroundColor: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // App Title
        Text(
            text = "GraftPredict",
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.35.sp,
            color = textSecondary
        )

        // Spacer for balance (matching HTML structure)
        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun ForgotPasswordLogoIcon(primaryBlue: Color) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(primaryBlue.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        // Using ShowChart icon as placeholder for monitor_heart
        Icon(
            imageVector = Icons.Default.ShowChart,
            contentDescription = "App Logo",
            tint = primaryBlue,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun ForgotPasswordEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textTertiary: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Label
        Text(
            text = "Email Address",
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            color = textPrimary
        )

        // Input Field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    text = "patient@example.com",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryBlue,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                cursorColor = primaryBlue,
                focusedLeadingIconColor = textTertiary,
                unfocusedLeadingIconColor = textTertiary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun BottomLoginPrompt(
    onLoginPressed: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    textSecondary: Color,
    primaryBlue: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(77.dp),
        color = backgroundColor,
        shadowElevation = 0.dp
    ) {
        Column {
            // Top Border
            Divider(
                color = borderColor.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // Prompt Text with Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 71.dp, vertical = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remember your password? ",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = onLoginPressed,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Log In",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = primaryBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
