package com.example.graftpredict.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {}
) {
    // Colors from HTML
    val primaryColor = Color(0xFF3B82F6)
    val primaryDark = Color(0xFF2652F1)
    val backgroundColor = Color(0xFF0F0F11)
    val surfaceDark = Color(0xFF1C1C1E)
    val textDark = Color(0xFFF9FAFB)
    val subtextDark = Color(0xFF9CA3AF)

    // Animation states
    val infiniteTransition = rememberInfiniteTransition()
    var contentVisible by remember { mutableStateOf(false) }

    // Pulsing animation for blobs
    val pulseAnimation1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val pulseAnimation2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing,
                delayMillis = 2000
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Fade-in animation for content
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    val contentOffset by animateDpAsState(
        targetValue = if (contentVisible) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    // Start animations on composition
    LaunchedEffect(Unit) {
        contentVisible = true
        // Navigate to login after 3 seconds
        delay(3000)
        onNavigateToLogin()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { onNavigateToLogin() }
    ) {
        // Background animated gradient blobs
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        // Top right blob
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.2f),
                                    primaryColor.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 1.2f, -size.height * 0.1f),
                                radius = size.width * 0.8f * pulseAnimation1
                            ),
                            radius = size.width * 0.8f * pulseAnimation1,
                            center = Offset(size.width * 1.2f, -size.height * 0.1f),
                            blendMode = BlendMode.Screen
                        )

                        // Bottom left blob
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8E63F1).copy(alpha = 0.2f), // Indigo
                                    Color(0xFF8E63F1).copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(-size.width * 0.2f, size.height * 1.1f),
                                radius = size.width * 0.6f * pulseAnimation2
                            ),
                            radius = size.width * 0.6f * pulseAnimation2,
                            center = Offset(-size.width * 0.2f, size.height * 1.1f),
                            blendMode = BlendMode.Screen
                        )
                    }
                }
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Animated content column
            Column(
                modifier = Modifier
                    .alpha(contentAlpha)
                    .offset(y = contentOffset)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // Logo with gradient border effect
                Box(
                    modifier = Modifier.size(192.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer gradient glow
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryDark,
                                    Color.Transparent
                                ),
                                radius = size.minDimension * 0.7f
                            ),
                            radius = size.minDimension * 0.7f,
                            center = center,
                            blendMode = BlendMode.Screen
                        )
                    }

                    // Logo container
                    var isImageLoading by remember { mutableStateOf(true) }
                    
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(48.dp))
                            .background(
                                color = Color.Black,
                                shape = RoundedCornerShape(48.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(48.dp)
                            )
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(48.dp),
                                ambientColor = Color.Black,
                                spotColor = Color.Black
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://lh3.googleusercontent.com/aida-public/AB6AXuAN7ILqsK488N8GuEx5I0-iL-Exj-uhYw0iJ-55FTyMWuzlhKQHoiwpKpsR7_TU2aKO1velwsPSV11cIikcud1tjlSn9YpzEs3uKyigDsZKIaTCcm6pU6yqt-wcIasI6_B4bw_v8WsoFU5oJ2ezlrHdea96H7fZe1djc-LkLbDpS-9QBAooW9SGYDy8eG7q8PJNu23PeNKmsOkJM_Jfg4xyKEUtqrA4PiIbO5vStItnAY0KaDXyOh7uHU1JPKFMRe8SuldNj1uGy7Rv")
                                .crossfade(true)
                                .allowHardware(true)
                                .build(),
                            contentDescription = "GraftPredict knee joint logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = 1.05f
                                    scaleY = 1.05f
                                },
                            onLoading = { _ ->
                                // Keep loading true
                            },
                            onSuccess = { _ ->
                                isImageLoading = false
                            },
                            onError = { _ ->
                                isImageLoading = false
                            }
                        )
                        
                        // Loading indicator
                        if (isImageLoading) {
                            CircularProgressIndicator(
                                color = primaryColor.copy(alpha = 0.6f),
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                // Text content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Graft")
                            withStyle(
                                SpanStyle(
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Predict")
                            }
                        },
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        "Advanced ACL injury analysis and graft sizing intelligence.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = subtextDark,
                        lineHeight = 24.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer with fade-in delay
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 300)),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        "FOR MEDICAL PROFESSIONAL USE ONLY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subtextDark.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )

                    // Horizontal line
                    Box(
                        modifier = Modifier
                            .width(128.dp)
                            .height(2.dp)
                            .clip(CircleShape)
                            .background(subtextDark.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

// Extension for alpha modifier
fun Modifier.alpha(alpha: Float): Modifier = this.graphicsLayer {
    this.alpha = alpha
}

// Animated visibility for content
@Composable
fun AnimatedContentVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it * 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
    ) {
        content()
    }
}
