@file:OptIn(ExperimentalMaterial3Api::class)

package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.theme.Manrope
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RegistrationScreen(
    onBackPressed: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRequestOtp: (fullName: String, email: String, password: String) -> Unit = { _, _, _ -> },
    navController: androidx.navigation.NavHostController? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Color palette from HTML
    val backgroundColor = Color(0xFF101922) // background-dark
    val surfaceColor = Color(0xFF192633) // surface-dark
    val borderColor = Color(0xFF324D67) // border-dark
    val primaryBlue = Color(0xFF137FEC) // primary
    val textPrimary = Color.White
    val textSecondary = Color(0xFF94A3B8) // slate-400
    val textTertiary = Color(0xFF92ADC9) // text-secondary-dark
    val placeholderColor = Color(0xFF92ADC9)

    // State variables
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }

    // Gender dropdown state
    var genderDropdownExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other")

    // ViewModel setup for OTP sending
    val session = remember { com.simats.graftpredict.data.local.SessionManager(context) }
    val api = remember { com.simats.graftpredict.data.api.ApiClient.create(session) }
    val repo = remember { com.simats.graftpredict.data.repository.AuthRepository(api) }
    val viewModel: com.simats.graftpredict.ui.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return com.simats.graftpredict.ui.viewmodel.AuthViewModel(repo, session) as T
            }
        }
    )

    // Listen to OTP sending state
    LaunchedEffect(viewModel) {
        viewModel.state.collectLatest { state ->
            when (state) {
                is com.simats.graftpredict.ui.viewmodel.UiState.Success -> {
                    if (isSendingOtp) {
                        isSendingOtp = false
                        // OTP sent successfully, navigate to verification screen
                        // Capitalize first and last names: first letter capital, rest lowercase
                        val capitalizedFirstName = firstName.trim().replaceFirstChar { it.uppercase() }
                        val capitalizedLastName = lastName.trim().split(" ").joinToString(" ") { word ->
                            word.replaceFirstChar { it.uppercase() }
                        }
                        val fullName = "$capitalizedFirstName $capitalizedLastName".trim()
                        if (navController != null) {
                            val encodedFullName = java.net.URLEncoder.encode(fullName, Charsets.UTF_8.name())
                            val encodedEmail = java.net.URLEncoder.encode(email.trim(), Charsets.UTF_8.name())
                            val encodedPassword = java.net.URLEncoder.encode(password.trim(), Charsets.UTF_8.name())
                            val encodedDob = java.net.URLEncoder.encode(dateOfBirth.trim(), Charsets.UTF_8.name())
                            val encodedGender = java.net.URLEncoder.encode(gender.trim(), Charsets.UTF_8.name())
                            navController.navigate("${com.simats.graftpredict.ui.navigation.Destinations.SignupVerify}/$encodedFullName/$encodedEmail/$encodedPassword/$encodedDob/$encodedGender")
                        } else {
                            onRequestOtp(fullName, email.trim(), password.trim())
                        }
                    }
                }
                is com.simats.graftpredict.ui.viewmodel.UiState.Error -> {
                    if (isSendingOtp) {
                        isSendingOtp = false
                        errorMsg = state.error
                    }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            // Top bar exactly like HTML
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        backgroundColor.copy(alpha = 0.9f),
                        androidx.compose.ui.graphics.RectangleShape
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onBackPressed)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Sign Up",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = textPrimary,
                    letterSpacing = (-0.015).sp,
                    modifier = Modifier.padding(end = 32.dp) // Matches HTML pr-8
                )

                Box(modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Brand Icon (orthopedics) - Exactly like HTML
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(surfaceColor),
                    contentAlignment = Alignment.Center
                ) {
                    // Using MedicalServices as orthopedics icon
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Orthopedics",
                        tint = primaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title - Exactly like HTML
                Text(
                    text = "Patient Registration",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.015).sp,
                    modifier = Modifier.padding(top = 24.dp)
                )

                // Subtitle - Exactly like HTML
                Text(
                    text = "Join to predict injury risks and manage your ACL recovery reports.",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 32.dp)
                        .widthIn(max = 320.dp)
                )

                // Form Fields - Exactly like HTML structure
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // First Name & Last Name Row - Exactly like HTML
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // First Name
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "First Name",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                color = textPrimary
                            )
                            CustomTextField(
                                value = firstName,
                                onValueChange = { newValue ->
                                    // Only allow alphabets, no spaces
                                    firstName = newValue.filter { it.isLetter() }
                                },
                                placeholder = "e.g. Alex",
                                surfaceColor = surfaceColor,
                                borderColor = borderColor,
                                primaryBlue = primaryBlue,
                                textPrimary = textPrimary,
                                placeholderColor = placeholderColor
                            )
                        }

                        // Last Name
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Last Name",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                color = textPrimary
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
                                    
                                    lastName = filtered
                                },
                                placeholder = "e.g. Johnson",
                                surfaceColor = surfaceColor,
                                borderColor = borderColor,
                                primaryBlue = primaryBlue,
                                textPrimary = textPrimary,
                                placeholderColor = placeholderColor
                            )
                        }
                    }

                    // Email Address - Exactly like HTML
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Email Address",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = textPrimary
                        )
                        EmailTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "name@example.com",
                            surfaceColor = surfaceColor,
                            borderColor = borderColor,
                            primaryBlue = primaryBlue,
                            textPrimary = textPrimary,
                            placeholderColor = placeholderColor
                        )
                    }

                    // Date of Birth & Gender Row - Exactly like HTML
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date of Birth
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Date of Birth",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                color = textPrimary
                            )
                            DateOfBirthTextField(
                                value = dateOfBirth,
                                onValueChange = { dateOfBirth = it },
                                placeholder = "YYYY-MM-DD",
                                surfaceColor = surfaceColor,
                                borderColor = borderColor,
                                primaryBlue = primaryBlue,
                                textPrimary = textPrimary,
                                placeholderColor = placeholderColor
                            )
                        }

                        // Gender Dropdown - Exactly like HTML
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Gender",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                color = textPrimary
                            )
                            GenderDropdown(
                                value = gender,
                                onValueChange = { gender = it },
                                placeholder = "Select",
                                genderOptions = genderOptions,
                                surfaceColor = surfaceColor,
                                borderColor = borderColor,
                                primaryBlue = primaryBlue,
                                textPrimary = textPrimary,
                                placeholderColor = placeholderColor
                            )
                        }
                    }

                    // Password Field - Exactly like HTML
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Password",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = textPrimary
                        )
                        PasswordTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "At least 8 characters",
                            isVisible = passwordVisible,
                            onToggleVisibility = { passwordVisible = !passwordVisible },
                            surfaceColor = surfaceColor,
                            borderColor = borderColor,
                            primaryBlue = primaryBlue,
                            textPrimary = textPrimary,
                            placeholderColor = placeholderColor
                        )
                    }

                    // Confirm Password Field - Exactly like HTML
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Confirm Password",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = textPrimary
                        )
                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Re-enter password",
                            isVisible = confirmPasswordVisible,
                            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                            surfaceColor = surfaceColor,
                            borderColor = borderColor,
                            primaryBlue = primaryBlue,
                            textPrimary = textPrimary,
                            placeholderColor = placeholderColor
                        )
                    }
                }

                // Create Account Button - Exactly like HTML
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp) // Matches HTML mt-10
                ) {
                    Button(
                        onClick = {
                            errorMsg = null

                            val firstNameTrim = firstName.trim()
                            val lastNameTrim = lastName.trim()
                            val mail = email.trim()
                            val pass = password.trim()
                            val confirm = confirmPassword.trim()

                            if (firstNameTrim.isEmpty() || lastNameTrim.isEmpty()) {
                                errorMsg = "First name and last name are required"
                                return@Button
                            }
                            if (mail.isEmpty()) {
                                errorMsg = "Email is required"
                                return@Button
                            }
                            if (dateOfBirth.isEmpty()) {
                                errorMsg = "Date of birth is required"
                                return@Button
                            }
                            if (gender.isEmpty()) {
                                errorMsg = "Gender is required"
                                return@Button
                            }
                            if (pass.isEmpty()) {
                                errorMsg = "Password is required"
                                return@Button
                            }
                            if (pass != confirm) {
                                errorMsg = "Passwords do not match"
                                return@Button
                            }
                            if (!isValidPassword(pass)) {
                                errorMsg = "Password must be ≥8 chars, include upper, lower, digit and special char, and no spaces"
                                return@Button
                            }

                            // Send OTP to email before navigating to verification
                            isSendingOtp = true
                            viewModel.signupSendOtp(mail)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = primaryBlue.copy(alpha = 0.5f)
                        ),
                        enabled = !isSendingOtp
                    ) {
                        if (isSendingOtp) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Create Account",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Error Message Display
                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = Color(0xFFFF6B6B),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                // Already a member? - Exactly like HTML
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp), // Matches HTML mt-6
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already a member?",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                    androidx.compose.material3.TextButton(
                        onClick = onLoginClick,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = "Log in",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = primaryBlue
                        )
                    }
                }

                // Bottom Policy Links - Exactly like HTML
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp), // Matches HTML pt-12
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = { navController?.navigate(com.simats.graftpredict.ui.navigation.Destinations.PrivacyPolicy) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Privacy Policy",
                            fontSize = 12.sp,
                            color = textSecondary.copy(alpha = 0.6f)
                        )
                    }
                    androidx.compose.material3.TextButton(
                        onClick = { navController?.navigate(com.simats.graftpredict.ui.navigation.Destinations.TermsAndConditions) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Terms of Service",
                            fontSize = 12.sp,
                            color = textSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    placeholderColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) primaryBlue else borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    placeholder,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = placeholderColor
                )
            },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    placeholderColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) primaryBlue else borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = {
                    Text(
                        placeholder,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = placeholderColor
                    )
                },
                interactionSource = interactionSource,
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Mail icon on right
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Mail,
                    contentDescription = "Mail",
                    tint = placeholderColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    placeholderColor: Color
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Calculate minimum date (14 years ago from today)
    val today = LocalDate.now()
    val minDate = today.minusYears(14)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) primaryBlue else borderColor,
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
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = {
                    Text(
                        placeholder,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = placeholderColor
                    )
                },
                interactionSource = interactionSource,
                shape = RoundedCornerShape(8.dp),
                readOnly = true,
                enabled = false,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    disabledTextColor = textPrimary,
                    cursorColor = primaryBlue,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = Manrope,
                    fontSize = 16.sp,
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
                    tint = placeholderColor,
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
                        onValueChange(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        showDatePicker = false
                    } else {
                        // Show error - date is too recent
                        android.widget.Toast.makeText(
                            context,
                            "You must be at least 14 years old to register",
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
    val datePickerState = remember {
        android.app.DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar.timeInMillis)
        }
    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    genderOptions: List<String>,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    placeholderColor: Color
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(
                width = if (isFocused || dropdownExpanded) 2.dp else 1.dp,
                color = if (isFocused || dropdownExpanded) primaryBlue else borderColor,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { dropdownExpanded = !dropdownExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text field showing selected value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (value.isEmpty()) placeholder else value,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = if (value.isEmpty()) placeholderColor else textPrimary
                )
            }

            // Expand icon on right
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ExpandMore,
                    contentDescription = "Expand",
                    tint = placeholderColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = textPrimary
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        dropdownExpanded = false
                    },
                    modifier = Modifier.background(surfaceColor)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    placeholderColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) primaryBlue else borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = {
                    Text(
                        placeholder,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = placeholderColor
                    )
                },
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                interactionSource = interactionSource,
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Visibility toggle on right
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isVisible) "Hide password" else "Show password",
                        tint = placeholderColor
                    )
                }
            }
        }
    }
}

/**
 * Validates password strength according to the same rules as Java LoginActivity:
 * - At least 8 characters
 * - No spaces
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character
 */
private fun isValidPassword(password: String): Boolean {
    if (password.isEmpty()) return false

    // No spaces
    if (password.contains(" ")) return false

    // At least 8 chars
    if (password.length < 8) return false

    // Check for required character types
    var hasUpper = false
    var hasLower = false
    var hasDigit = false
    var hasSpecial = false

    for (char in password) {
        when {
            char.isUpperCase() -> hasUpper = true
            char.isLowerCase() -> hasLower = true
            char.isDigit() -> hasDigit = true
            else -> hasSpecial = true
        }
    }

    return hasUpper && hasLower && hasDigit && hasSpecial
}
