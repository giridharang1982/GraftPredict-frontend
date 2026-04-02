package com.simats.graftpredict.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.ui.theme.Manrope
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.simats.graftpredict.ml.ACLPredictorWrapper
import android.util.Log
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onPredictClick: () -> Unit = {},
    onGraftSizingClick: () -> Unit = {},
    onManageReportsClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onPatientReportsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    var isPredictExpanded by remember { mutableStateOf(false) }
    var showTerminalOutput by remember { mutableStateOf(false) }

    // User role state - to control visibility of doctor-only features
    var userRole by remember { mutableStateOf("") }

    // Image selection state
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoadingPrediction by remember { mutableStateOf(false) }
    var terminalOutput by remember { mutableStateOf<List<String>>(emptyList()) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val session = remember { SessionManager(context) }
    val firstName = session.getFirstName() ?: "Alex"
    val aclPredictor = remember { ACLPredictorWrapper(context) }

    // LazyListState for controlling scroll position
    val lazyListState = rememberLazyListState()

    // Load user role on composition
    LaunchedEffect(Unit) {
        try {
            val cachedUserRole = session.getUserRole() ?: ""
            userRole = cachedUserRole
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Image selection launcher for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            selectedImageUris = uris
            Log.d("HomeScreen", "Selected ${uris.size} images")
        }
    )

    // Cleanup predictor on dispose
    DisposableEffect(Unit) {
        onDispose {
            aclPredictor.close()
        }
    }

    // Color palette from HTML
    val backgroundColor = Color(0xFF101922)
    val backgroundLight = Color(0xFFF6F7F8)
    val cardBackground = Color(0xFF1A2632)
    val cardBackgroundLight = Color.White
    val primaryBlue = Color(0xFF137FEC)
    val textPrimary = Color.White
    val textPrimaryDark = Color(0xFF0F172A)
    val textSecondary = Color(0xFF94A3B8)
    val textSecondaryLight = Color(0xFF64748B)
    val borderColor = Color(0xFF1E293B)
    val borderLight = Color(0xFFE2E8F0)
    val terminalBg = Color(0xFF0A0F14)
    val iconGray = Color(0xFF64748B)

    // Dark mode state (simulating HTML's dark class)
    val isDarkMode = true

    Scaffold(
        containerColor = if (isDarkMode) backgroundColor else backgroundLight,
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                color = (if (isDarkMode) backgroundColor else backgroundLight).copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo with orthopedics icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = primaryBlue.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Healing,
                            contentDescription = "Orthopedics",
                            tint = primaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // App Title
                    Text(
                        text = "Graft Predict",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 22.5.sp,
                        color = if (isDarkMode) textPrimary else textPrimaryDark
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Profile Icon Button
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "User Profile",
                            tint = if (isDarkMode) textPrimary else textPrimaryDark,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState, // Add state for scroll control
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                // Hero Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray) // Fallback background
                ) {
                    // Background Image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuAIn7I-9J5OAt-0Qj7kpL-QHhJTQTLud2RvyOyqScy5anMd1mnAI27txQ_kWpjWxJkecOr0q_MwEbUsHpCkPfCh9luNlV6xS5ZDpElk4iKzuErXPetxJPIs2rREWzh0tatP_EzuBhTpA-knLPriftEnwWNwIkeuzw_C8NZ58zSfkKpOrilCxY3i57wSE3bX-13LCjtcMQGbVYX1DbOmFcIRM6JOUsvKAt5vETflm2VUEDDCC86u409JWFxslq-nIBR9bHFSKsaRf4Yy")
                            .build(),
                        contentDescription = "Synthetic Graft Visualization",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        backgroundColor.copy(alpha = 0.2f),
                                        backgroundColor.copy(alpha = 0.9f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    // Content at bottom
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Recovery Phase Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(primaryBlue.copy(alpha = 0.9f))
                                .height(24.dp)
                        ) {
                            Text(
                                text = "Recovery Phase 1",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 0.5.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your Synthetic Graft",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 28.sp,
                            color = textPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Visualizing the positioning and stability of your replacement.",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            item {
                // Welcome Section
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Welcome back, $firstName",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        lineHeight = 38.sp,
                        letterSpacing = (-0.75).sp,
                        color = if (isDarkMode) textPrimary else textPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your knee health is stable. Let's continue tracking your recovery journey.",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = if (isDarkMode) textSecondary else textSecondaryLight
                    )
                }
            }

            // Key change: This item represents the Quick Actions section
            // We'll give it a key so we can easily scroll to it
            item(key = "quick_actions_section") {
                // Quick Actions Section
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Actions",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            color = if (isDarkMode) textPrimary else textPrimaryDark
                        )

                        TextButton(
                            onClick = {
                                // Scroll to this item (index 2) with an offset
                                // We need to scroll so that "Quick Actions" text touches the bottom of header
                                coroutineScope.launch {
                                    // The header is 64dp, and we have padding from scaffold
                                    // We want to scroll so that this item's top aligns with the bottom of header
                                    // Calculate the scroll position
                                    lazyListState.animateScrollToItem(
                                        index = 2, // This is the index of the Quick Actions section
                                        scrollOffset = 0
                                    )
                                }
                                onViewAllClick() // Call the original callback if needed
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = primaryBlue
                            )
                        ) {
                            Text(
                                text = "View All",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Predict Tear Risk Card (Expandable)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = null,
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            ) {
                                isPredictExpanded = !isPredictExpanded
                                if (!isPredictExpanded) {
                                    showTerminalOutput = false
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkMode) cardBackground else cardBackgroundLight,
                            contentColor = if (isDarkMode) textPrimary else textPrimaryDark
                        ),
                        border = BorderStroke(1.dp, if (isDarkMode) primaryBlue.copy(alpha = 0.4f) else borderLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Header (changes based on expansion)
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = if (isPredictExpanded) Arrangement.Center else Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isPredictExpanded) 64.dp else 48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF3B82F6).copy(alpha = if (isDarkMode) 0.2f else 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.MonitorHeart,
                                        contentDescription = "Vital Signs",
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(if (isPredictExpanded) 40.dp else 24.dp)
                                    )
                                }

                                if (!isPredictExpanded) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                }

                                Column(
                                    modifier = if (isPredictExpanded) Modifier.fillMaxWidth() else Modifier.weight(1f),
                                    horizontalAlignment = if (isPredictExpanded) Alignment.CenterHorizontally else Alignment.Start
                                ) {
                                    if (isPredictExpanded) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    Text(
                                        text = "Predict Tear Risk",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = if (isPredictExpanded) 20.sp else 16.sp,
                                        lineHeight = if (isPredictExpanded) 28.sp else 24.sp,
                                        color = if (isDarkMode) textPrimary else textPrimaryDark,
                                        textAlign = if (isPredictExpanded) TextAlign.Center else TextAlign.Start
                                    )

                                    if (!isPredictExpanded) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Analyze scans with dual-model diagnostic engine.",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = if (isDarkMode) textSecondary else textSecondaryLight
                                        )
                                    }
                                }

                                if (!isPredictExpanded) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Icon(
                                        Icons.Outlined.ChevronRight,
                                        contentDescription = "Expand",
                                        tint = iconGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Expanded Content
                            // Expanded Content
                            if (isPredictExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Title and image count
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Select MRI images for analysis",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = if (isDarkMode) textPrimary else textPrimaryDark
                                        )
                                        Text(
                                            text = "Selected images: ${selectedImageUris.size}",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            color = textSecondary
                                        )
                                    }

                                    // Camera and Gallery Buttons - CHANGED: Hide when terminal output is shown
                                    if (!showTerminalOutput) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    // Camera action
                                                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                                    (context as? Activity)?.startActivity(intent)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = if (isDarkMode) Color(0xFF374151).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                                                    contentColor = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155)
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                elevation = null
                                            ) {
                                                Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "Camera",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = Manrope
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    // Gallery action
                                                    galleryLauncher.launch("image/*")
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = if (isDarkMode) Color(0xFF374151).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                                                    contentColor = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155)
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                elevation = null
                                            ) {
                                                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "Gallery",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = Manrope
                                                )
                                            }
                                        }
                                    }

                                    // Image Preview Row - Only show if images are selected - CHANGED: Also hide when terminal output is shown
                                    if (!showTerminalOutput && selectedImageUris.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            selectedImageUris.forEachIndexed { index, uri ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(if (isDarkMode) Color(0xFF374151).copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                                        .border(
                                                            1.dp,
                                                            if (isDarkMode) borderColor else borderLight,
                                                            RoundedCornerShape(12.dp)
                                                        ),
                                                    contentAlignment = Alignment.TopEnd
                                                ) {
                                                    // Image preview
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(context)
                                                            .data(uri)
                                                            .crossfade(true)
                                                            .build(),
                                                        contentDescription = "Selected image ${index + 1}",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(12.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )

                                                    // Image number badge
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .background(primaryBlue, shape = CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = (index + 1).toString(),
                                                            color = Color.White,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Terminal Output or Predict Button
                                    if (showTerminalOutput) {
                                        TerminalOutput(
                                            terminalLines = terminalOutput,
                                            isLoading = isLoadingPrediction,
                                            modifier = Modifier.fillMaxWidth(),
                                            isDarkMode = isDarkMode
                                        )
                                    } else {
                                        // Predict Button
                                        Button(
                                            onClick = {
                                                if (selectedImageUris.isNotEmpty()) {
                                                    // Hide image selection UI and show terminal
                                                    showTerminalOutput = true
                                                    isLoadingPrediction = true
                                                    terminalOutput = emptyList()

                                                    // Run prediction in background
                                                    coroutineScope.launch(Dispatchers.Default) {
                                                        try {
                                                            val result = aclPredictor.predictFromImages(
                                                                selectedImageUris
                                                            ) { progressLine ->
                                                                terminalOutput = terminalOutput + progressLine
                                                            }

                                                            // Add result to terminal
                                                            terminalOutput = terminalOutput + result
                                                            isLoadingPrediction = false
                                                        } catch (e: Exception) {
                                                            Log.e("HomeScreen", "Prediction error", e)
                                                            terminalOutput = terminalOutput + "\n\$ ERROR: ${e.message}"
                                                            isLoadingPrediction = false
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = selectedImageUris.isNotEmpty(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = primaryBlue,
                                                contentColor = Color.White,
                                                disabledContainerColor = primaryBlue.copy(alpha = 0.5f)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 8.dp,
                                                pressedElevation = 4.dp
                                            )
                                        ) {
                                            Text(
                                                "Predict",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = Manrope,
                                                modifier = Modifier.padding(vertical = 12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Graft Sizing Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onGraftSizingClick),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkMode) cardBackground else cardBackgroundLight
                        ),
                        border = BorderStroke(1.dp, if (isDarkMode) borderColor else borderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF10B981).copy(alpha = if (isDarkMode) 0.2f else 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Straighten,
                                    contentDescription = "Graft Sizing",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Graft Sizing",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    color = if (isDarkMode) textPrimary else textPrimaryDark
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Calculate the optimal synthetic graft dimensions.",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = if (isDarkMode) textSecondary else textSecondaryLight
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = "Navigate",
                                tint = iconGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Manage Reports Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onManageReportsClick),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkMode) cardBackground else cardBackgroundLight
                        ),
                        border = BorderStroke(1.dp, if (isDarkMode) borderColor else borderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF8B5CF6).copy(alpha = if (isDarkMode) 0.2f else 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Topic,
                                    contentDescription = "Manage Reports",
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Manage Reports",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    color = if (isDarkMode) textPrimary else textPrimaryDark
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Store and export your medical recovery reports.",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = if (isDarkMode) textSecondary else textSecondaryLight
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = "Navigate",
                                tint = iconGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    // Patient's Reports Card - Only visible for doctors
                    if (userRole.equals("doctor", ignoreCase = true)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onPatientReportsClick),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkMode) cardBackground else cardBackgroundLight
                            ),
                            border = BorderStroke(1.dp, if (isDarkMode) borderColor else borderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF97316).copy(alpha = if (isDarkMode) 0.2f else 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = "Patient's Reports",
                                        tint = if (isDarkMode) Color(0xFFFB923C) else Color(0xFFF97316),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Patient's Reports",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        color = if (isDarkMode) textPrimary else textPrimaryDark
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Search and view reports shared by patients.",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        color = if (isDarkMode) textSecondary else textSecondaryLight
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Icon(
                                    Icons.Outlined.ChevronRight,
                                    contentDescription = "Navigate",
                                    tint = iconGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Daily Tip Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryBlue.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = primaryBlue,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Daily Tip",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = if (isDarkMode) textPrimary else textPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Consistency is key. Remember to log your pain levels daily for better prediction accuracy.",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = if (isDarkMode) textSecondary else textSecondaryLight
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ... rest of the code remains unchanged (TerminalOutput composable and related functions)
@Composable
fun TerminalOutput(
    terminalLines: List<String> = emptyList(),
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    // Parse the prediction result from terminal lines
    val parsedResult = remember(terminalLines) {
        parsePredictionResult(terminalLines)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0F14)
        ),
        border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Terminal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Yellow.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Green.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Dual-Model Core v2.4",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
            }

            // Terminal Content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Always show these initial messages
                TerminalLine(text = "Initializing MRI data scan...")
                TerminalLine(text = "Cross-referencing Model A (MRNet)...")

                // Show parsed results or loading state
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp, 4.dp)
                                .background(Color(0xFF137FEC))
                        )
                        Text(
                            text = "Analyzing images...",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else if (parsedResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Model A Results
                    if (parsedResult.mrnetPrediction.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Model A Analysis:",
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF60A5FA)
                            )
                            Text(
                                text = parsedResult.mrnetPrediction,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF60A5FA)
                            )
                        }

                        if (parsedResult.mrnetProbability != null) {
                            TerminalLine(
                                text = "MRNet Probability: ${String.format("%.3f", parsedResult.mrnetProbability)}",
                                indent = 12.dp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Model B Analysis
                    TerminalLine(text = "Cross-referencing Model B (KneeMRI)...")

                    if (parsedResult.kneeMRIPrediction.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Model B Analysis:",
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF8B5CF6)
                            )
                            Text(
                                text = parsedResult.kneeMRIPrediction,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF8B5CF6)
                            )
                        }

                        if (parsedResult.kneeMRIProbabilities != null && parsedResult.kneeMRIProbabilities.isNotEmpty()) {
                            TerminalLine(
                                text = "KneeMRI Probabilities: ${parsedResult.kneeMRIProbabilities.map { String.format("%.3f", it) }}",
                                indent = 12.dp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Aggregated Results Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (parsedResult.isStable) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f))
                            .border(
                                1.dp,
                                if (parsedResult.isStable) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Consensus Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (parsedResult.isStable) Icons.Outlined.Verified else Icons.Outlined.Warning,
                                    contentDescription = "Consensus",
                                    tint = if (parsedResult.isStable) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = parsedResult.consensusStatus.uppercase(),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = if (parsedResult.isStable) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            // Aggregated Risk
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Aggregated Tear Risk:",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${(parsedResult.aggregatedRisk * 100).toInt()}%",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (parsedResult.aggregatedRisk > 0.7) Color(0xFFEF4444)
                                    else if (parsedResult.aggregatedRisk > 0.3) Color(0xFFF59E0B)
                                    else Color(0xFF10B981)
                                )
                            }

                            // Confidence Match
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Confidence Match:",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = parsedResult.confidenceLevel,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (parsedResult.confidenceLevel.startsWith("High")) Color(0xFF10B981)
                                    else if (parsedResult.confidenceLevel.startsWith("Medium")) Color(0xFFF59E0B)
                                    else Color(0xFFEF4444)
                                )
                            }

                            // Warning if present
                            if (parsedResult.warning.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Error,
                                        contentDescription = "Warning",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = parsedResult.warning,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp,
                                        color = Color(0xFFF59E0B),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Ready message
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp, 4.dp)
                                .background(Color(0xFF137FEC))
                        )
                        Text(
                            text = "Ready for report generation...",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

// Data class to hold parsed prediction results
data class PredictionResult(
    val mrnetPrediction: String = "",
    val mrnetProbability: Double? = null,
    val kneeMRIPrediction: String = "",
    val kneeMRIProbabilities: List<Double>? = null,
    val warning: String = "",
    val consensusStatus: String = "Consensus Reached",
    val aggregatedRisk: Double = 0.0,
    val confidenceLevel: String = "High (96%)",
    val isStable: Boolean = true
)

// Function to parse prediction output from terminal lines
private fun parsePredictionResult(lines: List<String>): PredictionResult? {
    if (lines.isEmpty()) return null

    // Join all lines and look for the prediction pattern
    val fullText = lines.joinToString("\n")

    // Extract MRNet prediction
    val mrnetPrediction = Regex("ACL tear prediction:\\s*(.+)")
        .find(fullText)?.groupValues?.get(1)?.trim() ?: ""

    // Extract KneeMRI prediction
    val kneeMRIPrediction = Regex("Prediction of ACL tear degree:\\s*(.+)")
        .find(fullText)?.groupValues?.get(1)?.trim() ?: ""

    // Extract MRNet probability
    val mrnetProbability = Regex("MRNet Probability:\\s*(\\d+\\.\\d+)")
        .find(fullText)?.groupValues?.get(1)?.toDoubleOrNull()

    // Extract KneeMRI probabilities
    val kneeMRIProbabilitiesMatch = Regex("KneeMRI Probabilities:\\s*\\[([^\\]]+)\\]")
        .find(fullText)
    val kneeMRIProbabilities = kneeMRIProbabilitiesMatch?.groupValues?.get(1)
        ?.split(",")
        ?.mapNotNull { it.trim().toDoubleOrNull() }
        ?: emptyList()

    // Extract warning
    val warning = Regex("Warning:\\s*(.+)")
        .find(fullText)?.groupValues?.get(1)?.trim() ?: ""

    // Calculate aggregated risk and determine status
    val isHealthyMRNet = mrnetPrediction.contains("healthy", ignoreCase = true)
    val isHealthyKneeMRI = kneeMRIPrediction.contains("healthy", ignoreCase = true)

    // Calculate risk based on probabilities
    val mrnetRisk = when {
        mrnetProbability != null -> if (isHealthyMRNet) mrnetProbability else 1.0 - mrnetProbability
        isHealthyMRNet -> 0.1
        else -> 0.9
    }

    val kneeMRIRisk = when {
        kneeMRIProbabilities.isNotEmpty() -> {
            if (kneeMRIProbabilities.size >= 3) {
                // Sum of partial and full tear probabilities
                kneeMRIProbabilities.getOrNull(1)?.plus(kneeMRIProbabilities.getOrNull(2) ?: 0.0) ?: 0.0
            } else if (isHealthyKneeMRI) 0.1 else 0.9
        }
        else -> if (isHealthyKneeMRI) 0.1 else 0.9
    }

    val aggregatedRisk = (mrnetRisk + kneeMRIRisk) / 2.0

    // Determine consensus and confidence
    val consensusStatus = when {
        isHealthyMRNet && isHealthyKneeMRI -> "STABLE"
        (!isHealthyMRNet && mrnetPrediction.isNotEmpty()) && (!isHealthyKneeMRI && kneeMRIPrediction.isNotEmpty()) -> "INJURY DETECTED"
        warning.isNotEmpty() -> "CAUTION ADVISED"
        else -> "INCONCLUSIVE"
    }

    val isStable = consensusStatus == "STABLE"

    // Determine confidence level based on agreement
    val confidenceLevel = when {
        (isHealthyMRNet && isHealthyKneeMRI) ||
                (!isHealthyMRNet && !isHealthyKneeMRI) -> "High (96%)"
        (mrnetRisk - kneeMRIRisk).absoluteValue < 0.2 -> "Medium (75%)"
        else -> "Low (50%)"
    }

    return PredictionResult(
        mrnetPrediction = mrnetPrediction,
        mrnetProbability = mrnetProbability,
        kneeMRIPrediction = kneeMRIPrediction,
        kneeMRIProbabilities = kneeMRIProbabilities,
        warning = warning,
        consensusStatus = consensusStatus,
        aggregatedRisk = aggregatedRisk.coerceIn(0.0, 1.0),
        confidenceLevel = confidenceLevel,
        isStable = isStable
    )
}

@Composable
fun TerminalLine(
    text: String,
    indent: Dp = 0.dp,
    color: Color = Color(0xFFCBD5E1),
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(indent))
        Text(
            text = if (indent == 0.dp) "> $text" else "  $text",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            color = color,
            lineHeight = 16.sp
        )
    }
}
