package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.theme.Manrope
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.util.Log
import com.simats.graftpredict.data.api.ApiClient
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.data.models.GraftReport
import com.simats.graftpredict.ml.AutograftPredictor
import java.util.Calendar

@Composable
fun GraftSizeCalculatorScreen(
    onBackPressed: () -> Unit = {},
    onCalculate: () -> Unit = {}
) {
    // Color palette from Figma design
    val backgroundColor = Color(0xFF101922)
    val surfaceColor = Color(0xFF192633)
    val borderColor = Color(0xFF324D67)
    val primaryBlue = Color(0xFF137FEC)
    val textPrimary = Color.White
    val textSecondary = Color(0xFF92ADC9)
    val toggleBackground = Color(0xFF233648)
    val dividerColor = Color(0xFF151F2B)
    val textQuaternary = Color(0xFF94A3B8)

    // State variables
    var patientName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var affectedDate by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedSide by remember { mutableStateOf("Right Knee") }
    
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    
    var legLength by remember { mutableStateOf("") }
    var thighLength by remember { mutableStateOf("") }
    var circumferenceThigh by remember { mutableStateOf("") }
    
    var posteriorST by remember { mutableStateOf("") }
    var posteriorGracilis by remember { mutableStateOf("") }
    var lateralST by remember { mutableStateOf("") }
    var lateralGracilis by remember { mutableStateOf("") }
    
    // Error states
    var patientNameError by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf("") }
    var heightError by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf("") }
    var legLengthError by remember { mutableStateOf("") }
    var thighLengthError by remember { mutableStateOf("") }
    var circumferenceThighError by remember { mutableStateOf("") }
    var posteriorSTError by remember { mutableStateOf("") }
    var posteriorGracilisError by remember { mutableStateOf("") }
    var lateralSTError by remember { mutableStateOf("") }
    var lateralGracilisError by remember { mutableStateOf("") }
    
    // Dialog states
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessages by remember { mutableStateOf(listOf<String>()) }
    
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            GraftCalculatorHeader(
                onBackPressed = onBackPressed,
                onReset = {
                    patientName = ""
                    age = ""
                    affectedDate = ""
                    selectedGender = "Male"
                    selectedSide = "Right Knee"
                    height = ""
                    weight = ""
                    bmi = ""
                    legLength = ""
                    thighLength = ""
                    circumferenceThigh = ""
                    posteriorST = ""
                    posteriorGracilis = ""
                    lateralST = ""
                    lateralGracilis = ""
                },
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                textPrimary = textPrimary,
                primaryBlue = primaryBlue
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Patient Information Section
                PatientInformationSection(
                    patientName = patientName,
                    onPatientNameChange = { patientName = it },
                    age = age,
                    onAgeChange = { age = it },
                    affectedDate = affectedDate,
                    onAffectedDateChange = { affectedDate = it },
                    selectedGender = selectedGender,
                    onGenderChange = { selectedGender = it },
                    selectedSide = selectedSide,
                    onSideChange = { selectedSide = it },
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    toggleBackground = toggleBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(dividerColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Body Metrics Section
                BodyMetricsSection(
                    height = height,
                    onHeightChange = { height = it },
                    weight = weight,
                    onWeightChange = { weight = it },
                    bmi = bmi,
                    onBmiChange = { bmi = it },
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(dividerColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Limb Measurements Section
                LimbMeasurementsSection(
                    legLength = legLength,
                    onLegLengthChange = { legLength = it },
                    thighLength = thighLength,
                    onThighLengthChange = { thighLength = it },
                    circumferenceThigh = circumferenceThigh,
                    onCircumferenceThighChange = { circumferenceThigh = it },
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    textQuaternary = textQuaternary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(dividerColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tendon Measurements Section
                TendonMeasurementsSection(
                    posteriorST = posteriorST,
                    onPosteriorSTChange = { posteriorST = it },
                    posteriorGracilis = posteriorGracilis,
                    onPosteriorGracilisChange = { posteriorGracilis = it },
                    lateralST = lateralST,
                    onLateralSTChange = { lateralST = it },
                    lateralGracilis = lateralGracilis,
                    onLateralGracilisChange = { lateralGracilis = it },
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Calculate Button
        CalculateButton(
            onClick = {
                // Clear previous errors
                patientNameError = ""
                ageError = ""
                heightError = ""
                weightError = ""
                legLengthError = ""
                thighLengthError = ""
                circumferenceThighError = ""
                posteriorSTError = ""
                posteriorGracilisError = ""
                lateralSTError = ""
                lateralGracilisError = ""
                
                val errors = mutableListOf<String>()
                
                // Validate inputs
                if (patientName.isBlank() || patientName.length < 4) {
                    patientNameError = "Patient name is required and must be at least 4 characters"
                    errors.add("Patient Name: $patientNameError")
                }
                if (age.isBlank()) {
                    ageError = "Age is required"
                    errors.add("Age: $ageError")
                } else {
                    val ageValue = age.toIntOrNull()
                    if (ageValue == null || ageValue < 2 || ageValue > 200) {
                        ageError = "Age must be between 2 and 200"
                        errors.add("Age: $ageError")
                    }
                }
                if (height.isBlank()) {
                    heightError = "Height is required"
                    errors.add("Height: $heightError")
                } else {
                    val heightValue = height.toDoubleOrNull()
                    if (heightValue == null || heightValue < 30 || heightValue > 270) {
                        heightError = "Height must be between 30 and 270 cm"
                        errors.add("Height: $heightError")
                    }
                }
                if (weight.isBlank()) {
                    weightError = "Weight is required"
                    errors.add("Weight: $weightError")
                } else {
                    val weightValue = weight.toDoubleOrNull()
                    if (weightValue == null || weightValue < 10 || weightValue > 650) {
                        weightError = "Weight must be between 10 and 650 kg"
                        errors.add("Weight: $weightError")
                    }
                }
                if (legLength.isBlank()) {
                    legLengthError = "Leg length is required"
                    errors.add("Leg Length: $legLengthError")
                } else {
                    val legLengthValue = legLength.toDoubleOrNull()
                    if (legLengthValue == null || legLengthValue < 15 || legLengthValue > 120) {
                        legLengthError = "Leg length must be between 15 and 120 cm"
                        errors.add("Leg Length: $legLengthError")
                    }
                }
                if (thighLength.isBlank()) {
                    thighLengthError = "Thigh length is required"
                    errors.add("Thigh Length: $thighLengthError")
                } else {
                    val thighLengthValue = thighLength.toDoubleOrNull()
                    if (thighLengthValue == null || thighLengthValue < 10 || thighLengthValue > 80) {
                        thighLengthError = "Thigh length must be between 10 and 80 cm"
                        errors.add("Thigh Length: $thighLengthError")
                    }
                }
                if (circumferenceThigh.isBlank()) {
                    circumferenceThighError = "Circumference thigh is required"
                    errors.add("Circumference Thigh: $circumferenceThighError")
                } else {
                    val circumferenceThighValue = circumferenceThigh.toDoubleOrNull()
                    if (circumferenceThighValue == null || circumferenceThighValue < 10 || circumferenceThighValue > 80) {
                        circumferenceThighError = "Circumference thigh must be between 10 and 80 cm"
                        errors.add("Circumference Thigh: $circumferenceThighError")
                    }
                }
                
                // Validate tendon measurements (optional but if provided, must be in range)
                if (posteriorST.isNotBlank()) {
                    val posteriorSTValue = posteriorST.toDoubleOrNull()
                    if (posteriorSTValue == null || posteriorSTValue < 1 || posteriorSTValue > 20) {
                        posteriorSTError = "Posterior ST must be between 1 and 20 mm"
                        errors.add("Posterior ST: $posteriorSTError")
                    }
                }
                if (posteriorGracilis.isNotBlank()) {
                    val posteriorGracilisValue = posteriorGracilis.toDoubleOrNull()
                    if (posteriorGracilisValue == null || posteriorGracilisValue < 1 || posteriorGracilisValue > 20) {
                        posteriorGracilisError = "Posterior gracilis must be between 1 and 20 mm"
                        errors.add("Posterior Gracilis: $posteriorGracilisError")
                    }
                }
                if (lateralST.isNotBlank()) {
                    val lateralSTValue = lateralST.toDoubleOrNull()
                    if (lateralSTValue == null || lateralSTValue < 1 || lateralSTValue > 20) {
                        lateralSTError = "Lateral ST must be between 1 and 20 mm"
                        errors.add("Lateral ST: $lateralSTError")
                    }
                }
                if (lateralGracilis.isNotBlank()) {
                    val lateralGracilisValue = lateralGracilis.toDoubleOrNull()
                    if (lateralGracilisValue == null || lateralGracilisValue < 1 || lateralGracilisValue > 20) {
                        lateralGracilisError = "Lateral gracilis must be between 1 and 20 mm"
                        errors.add("Lateral Gracilis: $lateralGracilisError")
                    }
                }
                
                // If there are errors, show the dialog
                if (errors.isNotEmpty()) {
                    errorMessages = errors
                    showErrorDialog = true
                    return@CalculateButton
                }
                
                // Parse numeric values for calculation
                val heightValue = height.toDoubleOrNull() ?: 0.0
                val weightValue = weight.toDoubleOrNull() ?: 0.0
                val bmiValue = if (bmi.isEmpty()) {
                    if (heightValue > 0 && weightValue > 0) {
                        weightValue / ((heightValue / 100) * (heightValue / 100))
                    } else {
                        0.0
                    }
                } else {
                    bmi.toDoubleOrNull() ?: 0.0
                }
                val legLengthValue = legLength.toDoubleOrNull() ?: 0.0
                val thighLengthValue = thighLength.toDoubleOrNull() ?: 0.0
                val circumferenceThighValue = circumferenceThigh.toDoubleOrNull() ?: 0.0
                val posteriorSTValue = posteriorST.toDoubleOrNull() ?: 0.0
                val posteriorGracilisValue = posteriorGracilis.toDoubleOrNull() ?: 0.0
                val lateralSTValue = lateralST.toDoubleOrNull() ?: 0.0
                val lateralGracilisValue = lateralGracilis.toDoubleOrNull() ?: 0.0

                // Log calculation attempt
                Log.d("GraftCalculator", "✓ Starting calculation with patient: $patientName, age: $age")

                // Sync to backend
                syncReportToBackend(
                    context = context,
                    patientName = patientName,
                    age = age,
                    gender = selectedGender,
                    affectedSide = selectedSide,
                    affectedDate = affectedDate,
                    height = heightValue,
                    weight = weightValue,
                    bmi = bmiValue,
                    legLength = legLengthValue,
                    thighLength = thighLengthValue,
                    circumferenceThigh = circumferenceThighValue,
                    posteriorST = posteriorSTValue,
                    posteriorGracilis = posteriorGracilisValue,
                    lateralST = lateralSTValue,
                    lateralGracilis = lateralGracilisValue
                )

                // Navigate back after successful calculation
                onBackPressed()
            },
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            primaryBlue = primaryBlue,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF6B35),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Validation Required",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = textPrimary
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Please correct the following issues before proceeding:",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = textSecondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF3F3)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                errorMessages.forEachIndexed { index, error ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "•",
                                            color = Color(0xFFDC2626),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                                        )
                                        Text(
                                            text = error,
                                            color = Color(0xFF991B1B),
                                            fontSize = 14.sp,
                                            fontFamily = Manrope,
                                            lineHeight = 20.sp
                                        )
                                    }
                                    if (index < errorMessages.size - 1) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showErrorDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "OK",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                },
                containerColor = surfaceColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun GraftCalculatorHeader(
    onBackPressed: () -> Unit,
    onReset: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    textPrimary: Color,
    primaryBlue: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = 0.5f)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = textPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Title
        Text(
            text = "Graft Size Calculator",
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 22.5.sp,
            letterSpacing = (-0.27).sp,
            color = textPrimary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Reset Button
        TextButton(
            onClick = onReset,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Reset",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.24.sp,
                color = primaryBlue
            )
        }
    }
}

@Composable
private fun PatientInformationSection(
    patientName: String,
    onPatientNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    affectedDate: String,
    onAffectedDateChange: (String) -> Unit,
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    selectedSide: String,
    onSideChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    toggleBackground: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Header with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = primaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Patient Information",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 22.5.sp,
                letterSpacing = (-0.45).sp,
                color = textPrimary
            )
        }

        // Name Field
        InputFieldWithLabel(
            label = "Name",
            value = patientName,
            onValueChange = { newValue ->
                // Only validate character restrictions during typing, not length
                if (newValue.isEmpty() || newValue.all { it.isLetter() || it == ' ' } && !newValue.contains("  ")) {
                    onPatientNameChange(newValue)
                }
            },
            placeholder = "eg: John Smith",
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            primaryBlue = primaryBlue,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Age and Affected Date Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Age Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabel(
                    label = "Age",
                    value = age,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && (newValue.toIntOrNull() ?: 0) in 2..200)) {
                            onAgeChange(newValue)
                        }
                    },
                    placeholder = "eg: 25",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Affected Date Field
            Box(modifier = Modifier.weight(1.5f)) {
                DateInputField(
                    label = "Affected Date",
                    value = affectedDate,
                    onValueChange = onAffectedDateChange,
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(21.dp))

        // Gender Toggle
        Text(
            text = "Gender",
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = textSecondary,
            modifier = Modifier.padding(bottom = 9.dp)
        )

        val backgroundColor = Color(0xFF101922)
        ToggleButton(
            options = listOf("Male", "Female"),
            selectedOption = selectedGender,
            onOptionSelected = onGenderChange,
            backgroundColor = toggleBackground,
            selectedColor = backgroundColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Affected Side Toggle
        Text(
            text = "Affected Side",
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = textSecondary,
            modifier = Modifier.padding(bottom = 9.dp)
        )

        ToggleButton(
            options = listOf("Left Knee", "Right Knee"),
            selectedOption = selectedSide,
            onOptionSelected = onSideChange,
            backgroundColor = toggleBackground,
            selectedColor = backgroundColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
    }
}

@Composable
private fun BodyMetricsSection(
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    bmi: String,
    onBmiChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Header with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Accessibility,
                contentDescription = null,
                tint = primaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Body Metrics",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 22.5.sp,
                letterSpacing = (-0.45).sp,
                color = textPrimary
            )
        }

        // Height and Weight Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Height Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabelAndUnit(
                    label = "Height",
                    value = height,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                            onHeightChange(newValue)
                        }
                    },
                    unit = "cm",
                    placeholder = "eg: 170",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Weight Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabelAndUnit(
                    label = "Weight",
                    value = weight,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                            onWeightChange(newValue)
                        }
                    },
                    unit = "kg",
                    placeholder = "eg: 70",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI Field (Read-only, auto-calculated)
        BMIDisplay(
            height = height,
            weight = weight,
            bmi = bmi,
            onBmiChange = onBmiChange,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            primaryBlue = primaryBlue,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
    }
}

@Composable
private fun LimbMeasurementsSection(
    legLength: String,
    onLegLengthChange: (String) -> Unit,
    thighLength: String,
    onThighLengthChange: (String) -> Unit,
    circumferenceThigh: String,
    onCircumferenceThighChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textQuaternary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Header with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Straighten,
                contentDescription = null,
                tint = primaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Limb Measurements",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 22.5.sp,
                letterSpacing = (-0.45).sp,
                color = textPrimary
            )
        }

        // Leg Length Field
        InputFieldWithLabelUnitAndInfo(
            label = "Leg Length (cm)",
            value = legLength,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                    onLegLengthChange(newValue)
                }
            },
            unit = "cm",
            placeholder = "eg: 85",
            showInfo = true,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            primaryBlue = primaryBlue,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textQuaternary = textQuaternary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Thigh Length Field
        InputFieldWithLabelAndUnit(
            label = "Thigh Length (cm)",
            value = thighLength,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                    onThighLengthChange(newValue)
                }
            },
            unit = "cm",
            placeholder = "eg: 45",
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            primaryBlue = primaryBlue,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Circumference Thigh Field
        InputFieldWithLabelUnitAndInfo(
            label = "Circumference thigh (cm)",
            value = circumferenceThigh,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                    onCircumferenceThighChange(newValue)
                }
            },
            unit = "cm",
            placeholder = "eg: 56",
            showInfo = true,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            primaryBlue = primaryBlue,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textQuaternary = textQuaternary
        )
    }
}

@Composable
private fun TendonMeasurementsSection(
    posteriorST: String,
    onPosteriorSTChange: (String) -> Unit,
    posteriorGracilis: String,
    onPosteriorGracilisChange: (String) -> Unit,
    lateralST: String,
    onLateralSTChange: (String) -> Unit,
    lateralGracilis: String,
    onLateralGracilisChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Header with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShowChart,
                contentDescription = null,
                tint = primaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tendon Measurements",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 22.5.sp,
                letterSpacing = (-0.45).sp,
                color = textPrimary
            )
        }

        // Posterior ST and Posterior gracilis Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Posterior ST Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabelAndUnit(
                    label = "Posterior ST",
                    value = posteriorST,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                            onPosteriorSTChange(newValue)
                        }
                    },
                    unit = "mm",
                    placeholder = "eg: 8.5",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Posterior gracilis Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabelAndUnit(
                    label = "Posterior gracilis",
                    value = posteriorGracilis,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                            onPosteriorGracilisChange(newValue)
                        }
                    },
                    unit = "mm",
                    placeholder = "eg: 5.2",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lateral ST and Lateral gracilis Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lateral ST Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabelAndUnit(
                    label = "Lateral ST",
                    value = lateralST,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                            onLateralSTChange(newValue)
                        }
                    },
                    unit = "mm",
                    placeholder = "eg: 7.3",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Lateral gracilis Field
            Box(modifier = Modifier.weight(1f)) {
                InputFieldWithLabelAndUnit(
                    label = "Lateral gracilis",
                    value = lateralGracilis,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                            onLateralGracilisChange(newValue)
                        }
                    },
                    unit = "mm",
                    placeholder = "eg: 4.8",
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }
    }
}

@Composable
private fun BMIDisplay(
    height: String,
    weight: String,
    bmi: String,
    onBmiChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    // Auto-calculate BMI whenever height or weight changes
    LaunchedEffect(height, weight) {
        val heightValue = height.toDoubleOrNull() ?: return@LaunchedEffect
        val weightValue = weight.toDoubleOrNull() ?: return@LaunchedEffect
        
        if (heightValue > 0 && weightValue > 0) {
            val calculatedBMI = weightValue / ((heightValue / 100) * (heightValue / 100))
            onBmiChange(String.format("%.2f", calculatedBMI))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            text = "BMI",
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = textSecondary
        )

        OutlinedTextField(
            value = bmi.toString(),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            trailingIcon = {
                Text(
                    text = "kg/m²",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryBlue,
                unfocusedBorderColor = borderColor,
                disabledBorderColor = borderColor,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                disabledContainerColor = surfaceColor,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                disabledTextColor = textPrimary,
                cursorColor = primaryBlue
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun InputFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = textSecondary
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = textSecondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryBlue,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                cursorColor = primaryBlue
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun DateInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = textSecondary
        )

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    val today = Calendar.getInstance()
                    val datePicker = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedDate = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year)
                            onValueChange(selectedDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    // Limit date picker to today (no future dates)
                    datePicker.datePicker.maxDate = today.timeInMillis
                    datePicker.show()
                },
            placeholder = {
                Text(
                    text = "mm/dd/yyyy",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = textSecondary
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date",
                    tint = primaryBlue,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            val today = Calendar.getInstance()
                            val datePicker = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedDate = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year)
                                    onValueChange(selectedDate)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            // Limit date picker to today (no future dates)
                            datePicker.datePicker.maxDate = today.timeInMillis
                            datePicker.show()
                        }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryBlue,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                cursorColor = primaryBlue
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun InputFieldWithLabelAndUnit(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    placeholder: String = "eg: 0",
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = textSecondary,
            modifier = Modifier.padding(bottom = 9.dp)
        )

        Box {
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
                        fontSize = 16.sp,
                        color = textSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = borderColor,
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    focusedTextColor = textSecondary,
                    unfocusedTextColor = textSecondary,
                    cursorColor = primaryBlue
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )

            // Unit label
            Text(
                text = unit,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = textSecondary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun InputFieldWithLabelUnitAndInfo(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    placeholder: String = "eg: 0",
    showInfo: Boolean,
    surfaceColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textQuaternary: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 9.dp)
        ) {
            Text(
                text = label,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = textSecondary
            )
            if (showInfo) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = textQuaternary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Box {
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
                        fontSize = 16.sp,
                        color = textSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = borderColor,
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    focusedTextColor = textSecondary,
                    unfocusedTextColor = textSecondary,
                    cursorColor = primaryBlue
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )

            // Unit label
            Text(
                text = unit,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = textSecondary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun ToggleButton(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    backgroundColor: Color,
    selectedColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) selectedColor else Color.Transparent)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 0.dp,
                                color = Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = option,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = if (isSelected) textPrimary else textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculateButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    primaryBlue: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(81.dp),
        color = backgroundColor.copy(alpha = 0.95f),
        shadowElevation = 0.dp
    ) {
        Column {
            Divider(
                color = borderColor,
                thickness = 1.dp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 17.dp)
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calculate Graft Size",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.24.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Syncs graft report data to backend
 */
fun syncReportToBackend(
    context: Context,
    patientName: String,
    age: String,
    gender: String,
    affectedSide: String,
    affectedDate: String,
    height: Double,
    weight: Double,
    bmi: Double,
    legLength: Double,
    thighLength: Double,
    circumferenceThigh: Double,
    posteriorST: Double,
    posteriorGracilis: Double,
    lateralST: Double,
    lateralGracilis: Double
) {
    try {
        // Get JWT token from SessionManager (correct storage)
        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken() ?: run {
            Log.w("GraftCalculator", "✗ No auth token available - skipping backend sync")
            return
        }

        Log.d("GraftCalculator", "✓ Auth token retrieved: ${token.take(20)}...")

        // Format affected side
        val formattedSide = when {
            affectedSide.contains("left", ignoreCase = true) -> "left knee"
            affectedSide.contains("right", ignoreCase = true) -> "right knee"
            else -> affectedSide.lowercase()
        }

        Log.d("GraftCalculator", "✓ Formatted affected_side: '$formattedSide'")

        // Calculate BMI if needed (backend calculates it but we need it for calculations)
        val calculatedBmi = if (bmi > 0) bmi else weight / Math.pow(height / 100.0, 2.0)

        // Determine if female
        val isFemale = gender.contains("female", ignoreCase = true) || 
                       gender.contains("woman", ignoreCase = true)

        // Calculate all 7 graft measurements using exact clinical formulas
        val graftDiameter1 = AutograftPredictor.calculateGraftDiameter1(
            height, weight, legLength, thighLength, circumferenceThigh,
            posteriorST, posteriorGracilis, lateralST, lateralGracilis, isFemale
        )

        val graftDiameter2 = AutograftPredictor.calculateGraftDiameter2(
            height, weight, legLength, thighLength, circumferenceThigh, isFemale
        )

        val hamstringAutograft = AutograftPredictor.calculateHamstringAutograft(
            height, weight, isFemale, false  // Using 4-strand graft (not 5-strand)
        )

        val quadricepsTendonDiameter = AutograftPredictor.calculateQuadricepsTendonDiameter(
            thighLength, height
        )

        val minimumSTLength = AutograftPredictor.calculateMinimumSTLength(
            height
        )

        val predictedSTValue = AutograftPredictor.calculatePredictedSTValue(
            legLength
        )

        val gracilisLength = AutograftPredictor.calculateGracilisLength(
            height
        )

        Log.d("GraftCalculator", "✓ Calculations complete (using exact clinical formulas):")
        Log.d("GraftCalculator", "  - Graft Diameter 1: $graftDiameter1 mm")
        Log.d("GraftCalculator", "  - Graft Diameter 2: $graftDiameter2 mm")
        Log.d("GraftCalculator", "  - Hamstring Autograft: $hamstringAutograft mm")
        Log.d("GraftCalculator", "  - Quadriceps Tendon: $quadricepsTendonDiameter mm")
        Log.d("GraftCalculator", "  - Minimum ST Length: $minimumSTLength mm")
        Log.d("GraftCalculator", "  - Predicted ST Value: $predictedSTValue mm")
        Log.d("GraftCalculator", "  - Gracilis Length: $gracilisLength mm")

        // Create report object with calculated values
        // Convert date from mm/dd/yyyy to yyyy-MM-dd format
        val formattedDate = if (affectedDate.isNotEmpty()) {
            try {
                val parts = affectedDate.split("/")
                if (parts.size == 3) {
                    val month = parts[0]
                    val day = parts[1]
                    val year = parts[2]
                    "$year-$month-$day"
                } else {
                    "2026-01-19" // fallback date
                }
            } catch (e: Exception) {
                "2026-01-19"
            }
        } else {
            "2026-01-19"
        }
        
        val defaultTendonFill = 0.0001
        val posteriorStValue = if (posteriorST > 0.0) posteriorST else defaultTendonFill
        val posteriorGracilisValue = if (posteriorGracilis > 0.0) posteriorGracilis else defaultTendonFill
        val lateralStValue = if (lateralST > 0.0) lateralST else defaultTendonFill
        val lateralGracilisValue = if (lateralGracilis > 0.0) lateralGracilis else defaultTendonFill

        val report = GraftReport(
            name = patientName.ifEmpty { "Unknown" },
            age = age.toIntOrNull() ?: 0,
            gender = gender.ifEmpty { "Other" },
            affected_side = formattedSide,
            affected_date = formattedDate,
            height = height,
            weight = weight,
            bmi = calculatedBmi,
            leg_length = legLength,
            thigh_length = thighLength,
            circumference_thigh = circumferenceThigh,
            posterior_st = posteriorStValue,
            posterior_gracilis = posteriorGracilisValue,
            lateral_st = lateralStValue,
            lateral_gracilis = lateralGracilisValue,
            graft_diameter_1 = graftDiameter1,
            graft_diameter_2 = graftDiameter2,
            hamstring_autograft = hamstringAutograft,
            quadriceps_tendon_diameter = quadricepsTendonDiameter,
            minimum_st_length = minimumSTLength,
            predicted_st_value = predictedSTValue,
            gracilis_length = gracilisLength
        )

        Log.d("GraftCalculator", "✓ Report prepared: name=$patientName, age=$age, side=$formattedSide, date=$affectedDate")

        // Make API call in background
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val apiService = ApiClient.create(sessionManager)

                Log.d("GraftCalculator", "→ Making API call to POST /reports...")
                val response = apiService.createReport(report = report)

                if (response.report_id != null) {
                    Log.d("GraftCalculator", "✓ SUCCESS: Report saved to backend with ID: ${response.report_id}")
                } else {
                    Log.e("GraftCalculator", "✗ BACKEND ERROR: ${response.error ?: "Unknown error"}")
                }
            } catch (e: Exception) {
                Log.e("GraftCalculator", "✗ API CALL FAILED: ${e.message}")
                e.printStackTrace()
            }
        }

    } catch (e: Exception) {
        Log.e("GraftCalculator", "✗ ERROR PREPARING SYNC: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * Validates patient name:
 * - Only alphabets and spaces
 * - No repeating spaces
 * - Maximum 2 spaces (non-repeating)
 * - Minimum length 4 characters
 */
fun isValidPatientName(name: String): Boolean {
    // Allow empty input during typing
    if (name.isEmpty()) {
        return true
    }
    
    // Check minimum length
    if (name.length < 4) {
        return false
    }
    
    // Check if only contains alphabets and spaces
    if (!name.all { it.isLetter() || it == ' ' }) {
        return false
    }
    
    // Check for repeating spaces
    if (name.contains("  ")) {
        return false
    }
    
    // Count spaces (max 2)
    val spaceCount = name.count { it == ' ' }
    if (spaceCount > 2) {
        return false
    }
    
    // Allow intermediate typing - only alphabets and spaces validation
    
    return true
}

