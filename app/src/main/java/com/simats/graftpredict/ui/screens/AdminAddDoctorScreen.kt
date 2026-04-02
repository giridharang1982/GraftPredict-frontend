package com.simats.graftpredict.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.graftpredict.data.api.ApiClient
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.data.models.CreateDoctorRequest
import com.simats.graftpredict.ui.theme.Manrope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddDoctorScreen(
    navController: androidx.navigation.NavHostController? = null,
    onBackPressed: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val api = remember { ApiClient.create(session) }
    val scrollState = rememberScrollState()

    // Colors - Match AdminHomeScreen
    val backgroundColor = Color(0xFF101922) // Matches AdminHomeScreen background-dark
    val cardDark = Color(0xFF1A2530) // Matches AdminHomeScreen card-dark
    val borderDark = Color(0xFF1E293B) // Matches AdminHomeScreen border-dark
    val textSecondary = Color(0xFF94A3B8) // Matches AdminHomeScreen text-secondary
    val primaryBlue = Color(0xFF137FEC) // primary
    val textPrimary = Color.White
    val placeholderColor = Color(0xFF5C728A) // placeholder color
    val bottomNavBg = cardDark // Use cardDark for bottom nav to match AdminHomeScreen
    val bottomNavBorder = borderDark.copy(alpha = 0.5f)
    val bottomNavTextSecondary = textSecondary
    val red400 = Color(0xFFF87171) // red-400
    val greenSuccess = Color(0xFF10B981) // success

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
    val genderOptions = listOf("Male", "Female", "Other")
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Date picker state
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Max date: 14 years ago
    val maxCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -14)
    }

    // Function to show date picker
    fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                if (selectedCalendar.timeInMillis <= maxCalendar.timeInMillis) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    dateOfBirth = formatter.format(selectedCalendar.time)
                } else {
                    errorMsg = "Doctor must be at least 14 years old"
                }
            },
            year,
            month,
            dayOfMonth
        )
        datePicker.datePicker.maxDate = maxCalendar.timeInMillis
        datePicker.show()
    }

    // Success dialog
    if (showSuccessDialog) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(16.dp)
                    .clickable {
                        showSuccessDialog = false
                        onBackPressed()
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardDark)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .wrapContentWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = greenSuccess,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Doctor Account Created",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Doctor account has been successfully created",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap to close",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = primaryBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        contentColor = textPrimary,
        bottomBar = {
            BottomNavigationBar(
                backgroundColor = bottomNavBg,
                borderColor = bottomNavBorder,
                primaryBlue = primaryBlue,
                textPrimary = textPrimary,
                textSecondary = bottomNavTextSecondary,
                red400 = red400,
                onHomeClick = onHomeClick,
                onAddDoctorClick = {}, // Already on this screen
                onLogoutClick = onLogoutClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header - Exactly like HTML
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                        .border(1.dp, borderDark.copy(alpha = 0.3f), androidx.compose.ui.graphics.RectangleShape)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back button - Exactly like HTML
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onBackPressed)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIos,
                            contentDescription = "Back",
                            tint = textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                    }

                    // Title - Exactly like HTML
                    Text(
                        text = "Add New Doctor",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        color = textPrimary,
                        letterSpacing = (-0.025).sp
                    )

                    // Reset button - Exactly like HTML
                    Text(
                        text = "Reset",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryBlue,
                        modifier = Modifier
                            .clickable {
                                // Reset all fields
                                firstName = ""
                                lastName = ""
                                email = ""
                                dateOfBirth = ""
                                gender = ""
                                password = ""
                                confirmPassword = ""
                                errorMsg = null
                            }
                            .padding(8.dp)
                    )
                }

                // Main content - Exactly like HTML
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 120.dp), // Space for bottom button and nav
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // First Name & Last Name Row - Exactly like HTML
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                                fontSize = 14.sp,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            CustomTextField(
                                value = firstName,
                                onValueChange = { firstName = it.filter { c -> c.isLetter() } },
                                placeholder = "e.g. Alex",
                                surfaceColor = cardDark,
                                borderColor = borderDark,
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
                                fontSize = 14.sp,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            CustomTextField(
                                value = lastName,
                                onValueChange = {
                                    val newValue = it.filter { c -> c.isLetter() || c == ' ' }
                                    val noLeadingTrailingSpaces = newValue.trim()
                                    val noDoubleSpaces = noLeadingTrailingSpaces.replace(Regex(" +"), " ")
                                    lastName = noDoubleSpaces
                                },
                                placeholder = "e.g. Johnson",
                                surfaceColor = cardDark,
                                borderColor = borderDark,
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
                            fontSize = 14.sp,
                            color = textPrimary,
                            letterSpacing = 0.5.sp
                        )
                        EmailTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "name@example.com",
                            surfaceColor = cardDark,
                            borderColor = borderDark,
                            primaryBlue = primaryBlue,
                            textPrimary = textPrimary,
                            placeholderColor = placeholderColor
                        )
                    }

                    // Date of Birth & Gender Row - Exactly like HTML
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                                fontSize = 14.sp,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(cardDark)
                                    .border(1.dp, borderDark, RoundedCornerShape(8.dp))
                                    .clickable { showDatePickerDialog() }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (dateOfBirth.isEmpty()) "YYYY-MM-DD" else dateOfBirth,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = if (dateOfBirth.isEmpty()) placeholderColor else textPrimary
                                    )
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = placeholderColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
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
                                fontSize = 14.sp,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            GenderDropdown(
                                value = gender,
                                onValueChange = { gender = it },
                                placeholder = "Select",
                                genderOptions = genderOptions,
                                surfaceColor = cardDark,
                                borderColor = borderDark,
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
                            fontSize = 14.sp,
                            color = textPrimary,
                            letterSpacing = 0.5.sp
                        )
                        PasswordTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "At least 8 characters",
                            isVisible = passwordVisible,
                            onToggleVisibility = { passwordVisible = !passwordVisible },
                            surfaceColor = cardDark,
                            borderColor = borderDark,
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
                            fontSize = 14.sp,
                            color = textPrimary,
                            letterSpacing = 0.5.sp
                        )
                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Re-enter password",
                            isVisible = confirmPasswordVisible,
                            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                            surfaceColor = cardDark,
                            borderColor = borderDark,
                            primaryBlue = primaryBlue,
                            textPrimary = textPrimary,
                            placeholderColor = placeholderColor
                        )
                    }
                }
            }

            // Fixed bottom button - Exactly like HTML (bottom-16 = 64dp from bottom)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp) // Matches HTML bottom-16 (16 * 4 = 64dp)
                    .fillMaxWidth()
                    .height(56.dp) // Matches HTML h-14 (14 * 4 = 56dp)
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = primaryBlue.copy(alpha = 0.2f))
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

                        // Call API to create doctor
                        isCreating = true
                        GlobalScope.launch {
                            try {
                                val createRequest = CreateDoctorRequest(
                                    first_name = firstName.replaceFirstChar { it.uppercase() },
                                    last_name = lastName.split(" ").joinToString(" ") { word ->
                                        word.replaceFirstChar { it.uppercase() }
                                    },
                                    email = email,
                                    password = password,
                                    dob = dateOfBirth,
                                    gender = gender,
                                    user = "doctor"
                                )

                                val response = api.createDoctor(createRequest)

                                isCreating = false
                                if (response.error == null) {
                                    showSuccessDialog = true
                                } else {
                                    errorMsg = response.error ?: "Failed to create doctor"
                                }
                            } catch (e: Exception) {
                                isCreating = false
                                errorMsg = e.message ?: "Failed to create doctor"
                            }
                        }
                    },
                    enabled = !isCreating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = primaryBlue.copy(alpha = 0.5f)
                    )
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Create Doctor Account",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Error message display - below the button
            if (errorMsg != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = errorMsg ?: "",
                        color = red400,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardDark, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    backgroundColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    red400: Color,
    onHomeClick: () -> Unit,
    onAddDoctorClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(backgroundColor)
            .border(1.dp, borderColor, androidx.compose.ui.graphics.RectangleShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onHomeClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = textSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Home",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = textSecondary
                )
            }

            // Add Doctor Tab (Active)
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
                    tint = primaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add Doctor",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = primaryBlue
                )
            }

            // Logout Tab
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
                    modifier = Modifier.size(24.dp)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 0.dp, vertical = 0.dp),
            placeholder = {
                Text(
                    placeholder,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = placeholderColor
                )
            },
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
                fontSize = 14.sp,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
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
                    .fillMaxHeight()
                    .padding(horizontal = 0.dp, vertical = 0.dp),
                placeholder = {
                    Text(
                        placeholder,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = placeholderColor
                    )
                },
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Mail icon on right
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Mail,
                    contentDescription = "Mail",
                    tint = placeholderColor,
                    modifier = Modifier.size(20.dp)
                )
            }
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
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Clickable surface for the dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(surfaceColor)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .clickable { expanded = true },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (value.isEmpty()) placeholder else value,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (value.isEmpty()) placeholderColor else textPrimary
                )

                // Expand icon on right
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = placeholderColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f) // Slightly less than full width for better appearance
                .background(surfaceColor),
            offset = DpOffset(x = 16.dp, y = 4.dp)
        ) {
            genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (value == option) primaryBlue.copy(alpha = 0.1f) else Color.Transparent)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
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
                    .fillMaxHeight()
                    .padding(horizontal = 0.dp, vertical = 0.dp),
                placeholder = {
                    Text(
                        placeholder,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = placeholderColor
                    )
                },
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    fontSize = 14.sp,
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
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isVisible) "Hide password" else "Show password",
                        tint = placeholderColor
                    )
                }
            }
        }
    }
}

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
