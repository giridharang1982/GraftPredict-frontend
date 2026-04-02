package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.simats.graftpredict.ui.navigation.Destinations
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simats.graftpredict.ui.theme.Manrope

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpScreen(
    navController: NavHostController,
    email: String
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var otp by remember { mutableStateOf("") }
    var timeRemaining by remember { mutableStateOf(300) } // 5 minutes
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Colors from HTML (dark theme only as per requirement)
    val backgroundColor = Color(0xFF101922) // background-dark
    val surfaceDark = Color(0xFF192633) // surface-dark
    val borderDark = Color(0xFF324D67) // border-dark
    val textSecondaryDark = Color(0xFF92ADC9) // text-secondary-dark
    val primaryBlue = Color(0xFF137FEC) // primary
    val textPrimary = Color.White
    val textSecondary = Color(0xFF94A3B8)
    val placeholderDark = Color(0xFF64748B) // slate-600 for dark mode placeholder

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

    var verifying by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var resendInProgress by remember { mutableStateOf(false) }
    var resendMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.state.collectLatest { s ->
            when (s) {
                is com.simats.graftpredict.ui.viewmodel.UiState.Success -> {
                    if (verifying) {
                        verifying = false
                        navController.navigate("${Destinations.ForgotReset}/$email")
                    } else if (resendInProgress) {
                        resendMsg = s.message ?: "OTP resent"
                        resendInProgress = false
                    }
                }
                is com.simats.graftpredict.ui.viewmodel.UiState.Error -> {
                    if (verifying) {
                        errorMsg = s.error
                        verifying = false
                    } else if (resendInProgress) {
                        resendMsg = s.error
                        resendInProgress = false
                    }
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
            }
        }
    }

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
                        text = "Verify OTP",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Enter the 6-digit verification code sent to your email address.",
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

                        // OTP Input Field
                        OtpInputField(
                            otp = otp,
                            onOtpChange = { newOtp ->
                                otp = newOtp
                                if (newOtp.length == 6) {
                                    keyboardController?.hide()
                                }
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

                    // Verify Button
                    Button(
                        onClick = {
                            errorMsg = null
                            verifying = true
                            viewModel.verifyForgotOtp(email, otp)
                        },
                        enabled = !verifying,
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
                        if (verifying) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "Verify Code",
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
                            modifier = Modifier.fillMaxWidth(),
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
                                viewModel.sendForgotOtp(email)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = primaryBlue
                            )
                        ) {
                            Text(
                                text = if (resendInProgress) "Resending..." else "Resend",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = primaryBlue
                            )
                        }
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
                        onClick = { /* Handle update email */ }
                    ) {
                        Text(
                            text = "Update Email",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = primaryBlue
                        )
                    }
                    if (resendMsg != null) {
                        Text(
                            text = resendMsg ?: "",
                            color = primaryBlue,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Colors from HTML
    val backgroundColor = Color(0xFF192633) // surface-dark
    val borderColor = Color(0xFF324D67) // border-dark
    val textColor = Color.White
    val placeholderColor = Color(0xFF64748B) // slate-600 for dark mode
    val focusedBorderColor = Color(0xFF137FEC) // primary

    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(backgroundColor)
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        contentAlignment = Alignment.Center
    ) {
        // Visible representation - Shows dots for empty positions, digits for filled ones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val char = otp.getOrNull(i)
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (char != null) char.toString() else "•",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = if (char != null) textColor else placeholderColor
                    )
                }
            }
        }

        // Hidden text input to receive keyboard events
        BasicTextField(
            value = otp,
            onValueChange = { newValue ->
                // Accept only digits and limit to 6 chars
                val filtered = newValue.filter { it.isDigit() }.take(6)
                onOtpChange(filtered)
                if (filtered.length == 6) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier
                .matchParentSize()
                .focusRequester(focusRequester)
                .padding(horizontal = 16.dp)
                .alpha(0f), // Hide the editable text
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center,
                color = textColor
            )
        )
    }
}
