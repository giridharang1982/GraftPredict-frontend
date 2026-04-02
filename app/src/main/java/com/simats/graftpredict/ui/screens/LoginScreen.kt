package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.theme.Manrope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.graftpredict.data.api.ApiClient
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.data.repository.AuthRepository
import com.simats.graftpredict.ui.viewmodel.AuthViewModel
import com.simats.graftpredict.ui.viewmodel.UiState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onAdminLoginSuccess: () -> Unit = {},
    onForgot: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {}
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

    var isLoggingIn by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.state.collectLatest { state ->
            when (state) {
                is UiState.Success -> {
                    if (isLoggingIn) {
                        isLoggingIn = false
                        errorMsg = null
                        // Check user role and navigate accordingly
                        val userRole = session.getUserRole()
                        if (userRole?.lowercase() == "admin") {
                            onAdminLoginSuccess()
                        } else {
                            onLoginSuccess()
                        }
                    }
                }
                is UiState.Error -> {
                    if (isLoggingIn) {
                        isLoggingIn = false
                        errorMsg = state.error
                    }
                }
                else -> Unit
            }
        }
    }
    // Color palette from Figma design
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
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
            // Top Bar
            LoginTopBar(
                onBackPressed = onBackPressed,
                backgroundColor = backgroundColor,
                textSecondary = textSecondary
            )

            // Content Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Icon
                LogoIcon(primaryBlue = primaryBlue)

                Spacer(modifier = Modifier.height(24.dp))

                // Welcome Heading
                Text(
                    text = "Welcome Back",
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
                    text = "Sign in to access your ACL recovery plan and manage your reports.",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Email Field
                InputField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "patient@example.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                PasswordField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password",
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textTertiary = textTertiary,
                    textQuaternary = textQuaternary
                )

                Spacer(modifier = Modifier.height(1.dp))

                // Forgot Password Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onForgot,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Forgot Password?",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = primaryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(1.dp))

                // Log In Button
                Button(
                    onClick = {
                        errorMsg = null

                        val trimmedEmail = email.trim()
                        val trimmedPassword = password.trim()

                        if (trimmedEmail.isEmpty()) {
                            errorMsg = "Email is required"
                            return@Button
                        }
                        if (trimmedPassword.isEmpty()) {
                            errorMsg = "Password is required"
                            return@Button
                        }

                        // Debug bypass to match legacy Java behavior (development only)
                        if (trimmedEmail == "debug@gmail.com" && trimmedPassword == "user123") {
                            session.saveSession(
                                token = "debug-token",
                                userId = "0",
                                firstName = "Debug",
                                lastName = "User",
                                email = trimmedEmail,
                                userRole = "patient"
                            )
                            onLoginSuccess()
                            return@Button
                        }

                        isLoggingIn = true
                        viewModel.login(trimmedEmail, trimmedPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoggingIn,
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
                        text = if (isLoggingIn) "Logging in..." else "Log In",
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
                }

            }
        }

        // Bottom CTA - Fixed at bottom
        BottomSignUpPrompt(
            onCreateAccount = onCreateAccount,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            textSecondary = textSecondary,
            primaryBlue = primaryBlue,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun LoginTopBar(
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

        // Spacer for balance
        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun LogoIcon(primaryBlue: Color) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(primaryBlue.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShowChart,
            contentDescription = "App Logo",
            tint = primaryBlue,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
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
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            color = textPrimary
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textTertiary: Color,
    textQuaternary: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            color = textPrimary
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasswordVisibilityChange,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = textQuaternary,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                unfocusedLeadingIconColor = textTertiary,
                focusedTrailingIconColor = textQuaternary,
                unfocusedTrailingIconColor = textQuaternary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun DividerWithText(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textQuaternary: Color
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = borderColor
        )

        Surface(
            color = backgroundColor,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = text,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.6.sp,
                color = textQuaternary,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun BottomSignUpPrompt(
    onCreateAccount: () -> Unit,
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
            Divider(
                color = borderColor.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New to GraftPredict? ",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onCreateAccount,
                    modifier = Modifier
                        .height(36.dp)
                        .wrapContentWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = primaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text = "Create Account",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = primaryBlue,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
