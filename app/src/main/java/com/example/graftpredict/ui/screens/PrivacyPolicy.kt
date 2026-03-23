package com.example.graftpredict.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graftpredict.ui.theme.GraftpredictTheme
import com.example.graftpredict.ui.theme.*

// --- Main Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = Slate800
                        ),
                        shape = RectangleShape
                    ),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onBackClick() }
                            .background(
                                color = Slate800,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // Title and date
                    Column {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 30.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Last Updated: March 16, 2026",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Section 1
                item { PrivacySection1() }
                // Section 2
                item { PrivacySection2() }
                // Section 3
                item { PrivacySection3() }
                // Section 4
                item { PrivacySection4() }
                // Section 5
                item { PrivacySection5() }
                // Section 6 & 7
                item { PrivacySection6And7() }
                // Section 8 Contact
                item { PrivacySection8() }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

// Section 1: Information We Collect
@Composable
fun PrivacySection1() {
    SectionHeader(number = "1", title = "Information We Collect")

    Column(modifier = Modifier.padding(start = 36.dp)) { // align with header content
        // Personal Information
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = if (false) Slate800 else Slate200,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        BulletList(items = listOf("Full name and email address", "User role (Patient or Practitioner)"))

        Spacer(modifier = Modifier.height(12.dp))

        // Medical & Anthropometric Data
        Text(
            text = "Medical & Anthropometric Data",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = if (false) Slate800 else Slate200,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        BulletList(
            items = listOf(
                "MRI scans (processed locally for privacy)",
                "Diagnostic reports and history",
                "Height, Weight, Age, Gender",
                "Limb measurements: Leg length, Thigh diameter, Calf diameter"
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Professional Credentials
        Text(
            text = "Professional Credentials",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = if (false) Slate800 else Slate200,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        BulletList(items = listOf("Verification documents for medical professionals"))
    }
}

// Section 2: How We Use Your Information
@Composable
fun PrivacySection2() {
    SectionHeader(number = "2", title = "How We Use Your Information")
    Column(modifier = Modifier.padding(start = 36.dp)) {
        BulletList(
            items = listOf(
                "Clinical Analysis: To provide accurate graft size predictions.",
                "Mathematical Modeling: To refine predictive algorithms for surgical planning.",
                "Report Generation: Creating downloadable PDF assessments for patients.",
                "Account Management: Secure authentication and profile synchronization."
            ),
            useBoldPrefix = true
        )
    }
}

// Section 3: Data Storage and Security
@Composable
fun PrivacySection3() {
    SectionHeader(number = "3", title = "Data Storage and Security")
    Column(modifier = Modifier.padding(start = 36.dp)) {
        // Highlight card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (false) Slate50 else Slate900.copy(alpha = 0.5f)
            ),
            border = BorderStroke(1.dp, if (false) Slate200 else Slate800),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("On-Device Processing: ")
                    append("Sensitive MRI visual data is processed locally on your hardware and is not uploaded to our central servers.")
                },
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (false) Slate600 else Slate400
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BulletList(
            items = listOf(
                "Encryption: All data in transit is protected via Transport Layer Security (TLS).",
                "Cloud Storage: Reports and basic profile information are stored in secure, encrypted cloud partitions."
            )
        )
    }
}

// Section 4: Data Sharing and Disclosure
@Composable
fun PrivacySection4() {
    SectionHeader(number = "4", title = "Data Sharing and Disclosure")
    Column(modifier = Modifier.padding(start = 36.dp)) {
        BulletList(
            items = listOf(
                "User-Initiated Sharing: Patients maintain full control over who can view their data.",
                "Doctor Access: Only granted upon explicit sharing by the patient.",
                "Legal Compliance: We do not sell or trade medical or personal data to third-party advertisers."
            )
        )
    }
}

// Section 5: Your Rights and Choices (cards)
@Composable
fun PrivacySection5() {
    SectionHeader(number = "5", title = "Your Rights and Choices")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple(Icons.Outlined.EditNote, "Access & Correction", "Review and update your biometric profile at any time."),
            Triple(Icons.Outlined.Delete, "Data Deletion", "Request permanent removal of your account and medical history."),
            Triple(Icons.Outlined.Block, "Revoking Access", "Instantly stop sharing data with specific practitioners.")
        ).forEach { (icon, title, desc) ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (false) Slate100 else Slate800.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            lineHeight = 12.sp
                        ),
                        color = if (false) Slate500 else Slate400
                    )
                }
            }
        }
    }
}

// Section 6 & 7
@Composable
fun PrivacySection6And7() {
    Column(
        modifier = Modifier.padding(start = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 6
        Column {
            Text(
                text = "6. Compliance with Medical Standards",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "GraftPredict adheres to international standards for electronic health record privacy and data security management.",
                style = MaterialTheme.typography.bodySmall,
                color = if (false) Slate600 else Slate400,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        // 7
        Column {
            Text(
                text = "7. Changes to This Policy",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "We may update this policy periodically. Users will be notified of significant changes via email or app notification.",
                style = MaterialTheme.typography.bodySmall,
                color = if (false) Slate600 else Slate400,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

// Section 8: Contact Us
@Composable
fun PrivacySection8() {
    SectionHeader(number = "8", title = "Contact Us")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp),
        colors = CardDefaults.cardColors(
            containerColor = Primary.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeveloperMode,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Lead Developer",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = if (false) Slate500 else Slate400
                    )
                    Text(
                        text = "Giridharan G",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Institution",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = if (false) Slate500 else Slate400
                    )
                    Text(
                        text = "Saveetha Institute of Medical and Technical Sciences",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

// --- Helper composables ---

@Composable
fun SectionHeader(number: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun BulletList(items: List<String>, useBoldPrefix: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (false) Slate600 else Slate400,
                    modifier = Modifier.width(12.dp)
                )
                if (useBoldPrefix && item.contains(":")) {
                    val parts = item.split(":", limit = 2)
                    Text(
                        text = buildAnnotatedString {
                            pushStyle(
                                androidx.compose.ui.text.SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = if (false) Slate800 else Slate200
                                )
                            )
                            append(parts[0] + ":")
                            pop()
                            append(parts[1])
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (false) Slate600 else Slate400
                    )
                } else {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (false) Slate600 else Slate400
                    )
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true, backgroundColor = 0xFF101922)
@Composable
fun PreviewPrivacyPolicyScreen() {
    GraftpredictTheme(darkTheme = true) {
        PrivacyPolicyScreen(onBackClick = {})
    }
}
