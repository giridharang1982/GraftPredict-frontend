package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.theme.GraftpredictTheme
import com.simats.graftpredict.ui.theme.*

// --- Data for expandable sections ---
data class TermsSection(
    val number: String,
    val title: String,
    val content: String
)

private val sections = listOf(
    TermsSection("01", "Description of Service", "GraftPredict provides advanced medical predictive analytics for graft success and patient monitoring. The platform leverages machine learning models to assist in data-driven decision-making processes for transplant procedures."),
    TermsSection("02", "Medical Disclaimer", "The predictions and analytics provided are for informational purposes only. GraftPredict is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of qualified healthcare providers."),
    TermsSection("03", "User Roles", "Access is restricted to licensed medical professionals and authorized clinical researchers. Users are responsible for maintaining the confidentiality of their account credentials and for all activities that occur under their account."),
    TermsSection("04", "Data Privacy", "We adhere to strict HIPAA and GDPR guidelines. All patient data is encrypted in transit and at rest. Personal identifiable information (PII) is handled with the highest level of security and only used for service delivery."),
    TermsSection("05", "Intellectual Property", "The algorithms, UI design, and proprietary datasets remain the exclusive property of Giridharan G. and GraftPredict. Users are granted a limited, non-exclusive license for clinical use."),
    TermsSection("06", "Prohibited Conduct", "Users may not reverse-engineer the application, use it for unauthorized clinical trials, or attempt to bypass any security protocols. Any misuse of patient data is strictly prohibited and subject to legal action."),
    TermsSection("07", "Limitation of Liability", "GraftPredict shall not be held liable for any direct, indirect, or consequential damages resulting from clinical decisions made using the platform's predictive metrics."),
    TermsSection("08", "Modifications", "We reserve the right to update these terms at any time. Continued use of the application after such changes constitutes acceptance of the new terms."),
    TermsSection("09", "Termination", "We may terminate or suspend access to our service immediately, without prior notice or liability, for any reason whatsoever, including breach of terms."),
    TermsSection("10", "Contact Information", "For any questions regarding these Terms and Conditions, please contact our support team at support@graftpredict.com.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBackClick: () -> Unit,
    onAcceptClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedStates = remember { mutableStateListOf<Boolean>().apply {
        // first section expanded by default
        addAll(sections.indices.map { it == 0 })
    } }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with blur effect (simulated via semi-transparent background)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                        shape = RectangleShape
                    )
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (false) Slate200 else Slate800
                        ),
                        shape = RectangleShape
                    ),
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (false) Slate100 else Slate800,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Primary
                        )
                    }

                    Text(
                        text = "Terms and Conditions",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Spacer for symmetry (placeholder)
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Last Updated box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Primary.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        )
                        .border(
                            width = 4.dp,
                            color = Primary,
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Last Updated",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (false) Slate500 else Slate400,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "March 16, 2026",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Intro text
                Column {
                    Row {
                        Text(
                            text = "Welcome to ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (false) Slate700 else Slate300
                        )
                        Text(
                            text = "GraftPredict",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = Primary
                        )
                        Text(
                            text = ". Developer: Giridharan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (false) Slate700 else Slate300
                        )
                    }
                    Text(
                        text = "Please read these terms carefully before using the application. By accessing or using GraftPredict, you agree to be bound by these Terms and Conditions. This service is designed to assist healthcare professionals but does not replace clinical judgment.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (false) Slate700 else Slate300
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Expandable sections
                sections.forEachIndexed { index, section ->
                    TermsExpandableItem(
                        section = section,
                        isExpanded = expandedStates[index],
                        onToggle = { expandedStates[index] = !expandedStates[index] }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Full agreement box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (false) Slate100 else Slate800.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (false) Slate200 else Slate800
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Full Agreement Content",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This document serves as the full and final agreement between the user and GraftPredict. By tapping 'Accept' below or by continued use of the platform, you acknowledge that you have read, understood, and agree to all clauses outlined above. This agreement is governed by international healthcare software standards and data protection laws.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (false) Slate600 else Slate400,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Footer
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (false) Slate200 else Slate800
                        ),
                        shape = RectangleShape
                    ),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onAcceptClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = "Accept Terms and Conditions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "© 2026 GraftPredict by Giridharan G. All rights reserved.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (false) Slate500 else Slate500
                    )
                }
            }
        }
    }
}

@Composable
fun TermsExpandableItem(
    section: TermsSection,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isExpanded) Primary.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (false) Slate100.copy(alpha = 0.5f) else Slate800.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Number circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = Primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = section.number,
                            color = Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (isExpanded) 180f else 0f),
                    tint = if (false) Slate400 else Slate400
                )
            }

            // Content (only if expanded)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = section.content,
                    modifier = Modifier.padding(start = 40.dp), // align with title text after circle
                    style = MaterialTheme.typography.bodySmall,
                    color = if (false) Slate600 else Slate400,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101922)
@Composable
fun PreviewTermsAndConditions() {
    GraftpredictTheme(darkTheme = true) {
        TermsAndConditionsScreen(
            onBackClick = {},
            onAcceptClick = {}
        )
    }
}
