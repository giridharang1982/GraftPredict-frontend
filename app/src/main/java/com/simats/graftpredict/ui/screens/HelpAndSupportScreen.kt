package com.simats.graftpredict.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.graftpredict.ui.theme.Slate400
import com.simats.graftpredict.ui.theme.Slate700
import com.simats.graftpredict.ui.theme.Slate800
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview

private val Primary = Color(0xFF137FEC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndSupportScreen(
    onBackClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header (sticky)
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
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                color = Slate800,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Help & Support",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Intro
                item {
                    SectionIntro()
                }

                // How to Use
                item {
                    HowToUseSection()
                }

                // FAQ
                item {
                    FAQSection()
                }

                // Contact Us
                item {
                    ContactSection()
                }

                // Legal & Privacy
                item {
                    LegalSection(onTermsClick = onTermsClick, onPrivacyClick = onPrivacyClick)
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

// --- Intro Section ---
@Composable
fun SectionIntro() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Hi there!",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We’re here to ensure your experience with GraftPredict is as smooth as possible. If you have any questions or need assistance, you're in the right place.",
            style = MaterialTheme.typography.bodyLarge,
            color = Slate400,
            lineHeight = 24.sp
        )
    }
}

// --- How to Use Section (4 cards) ---
@Composable
fun HowToUseSection() {
    SectionHeader(icon = Icons.Outlined.MenuBook, title = "How to Use GraftPredict")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple(Icons.Outlined.FileUpload, "Upload MRI", "Securely upload your DICOM or high-res scan files."),
            Triple(Icons.Outlined.Straighten, "Enter Measurements", "Provide specific anatomical dimensions for higher accuracy."),
            Triple(Icons.Outlined.Analytics, "Generate Report", "AI processes the data to predict optimal graft outcomes."),
            Triple(Icons.Outlined.Share, "Share with your Doctor", "Export a PDF report to review with your surgical team.")
        ).forEach { (icon, title, desc) ->
            HelpCard(icon = icon, title = title, description = desc)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun HelpCard(icon: ImageVector, title: String, description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Slate800.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Slate400,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// --- FAQ Section (expandable) ---
@Composable
fun FAQSection() {
    SectionHeader(icon = Icons.Outlined.Help, title = "Frequently Asked Questions")

    val faqItems = listOf(
        "Is this a final medical diagnosis?" to "No. GraftPredict provides AI-assisted predictions intended to support clinical decision-making. All results must be reviewed and confirmed by a qualified surgeon.",
        "Where is my MRI data stored?" to "Your data is encrypted and stored on HIPAA-compliant cloud servers. You can delete your scans at any time from the account settings.",
        "Why do you need my leg diameter?" to "Anatomical scaling helps the AI normalize MRI features against your body's physical proportions, significantly increasing the accuracy of the prediction model."
    )

    val expandedStates = remember { mutableStateListOf(false, false, false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        faqItems.forEachIndexed { index, (question, answer) ->
            FAQItem(
                question = question,
                answer = answer,
                isExpanded = expandedStates[index],
                onToggle = { expandedStates[index] = !expandedStates[index] }
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun FAQItem(question: String, answer: String, isExpanded: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = Slate800
                ),
                shape = RectangleShape
            )
            .padding(vertical = 12.dp, horizontal = 0.dp) // border only bottom? We'll simulate border-bottom with a divider after.
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(if (isExpanded) 180f else 0f),
                tint = Slate400
            )
        }
        if (isExpanded) {
            Text(
                text = answer,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Slate400,
                lineHeight = 18.sp
            )
        }
        // Divider at bottom
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = Slate800,
            thickness = 1.dp
        )
    }
}

// --- Contact Us Section ---
@Composable
fun ContactSection() {
    SectionHeader(icon = Icons.Outlined.Mail, title = "Contact Us")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        color = Primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Have a specific technical issue or billing question?",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "support@graftpredict.com",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Primary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Average response time: < 24 hours",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

// --- Legal & Privacy Section ---
@Composable
fun LegalSection(onTermsClick: () -> Unit, onPrivacyClick: () -> Unit) {
    SectionHeader(icon = Icons.Outlined.Gavel, title = "Legal & Privacy")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Your privacy is our priority. We adhere to the highest standards of data protection and ethical medical AI practices.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate400,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Terms button
            Surface(
                modifier = Modifier.weight(1f),
                color = Slate800,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Slate700)
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onTermsClick() }
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Terms & Conditions",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            // Privacy button
            Surface(
                modifier = Modifier.weight(1f),
                color = Slate800,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Slate700)
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onPrivacyClick() }
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

// --- Helper: Section Header with icon ---
@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// --- Preview ---
@Preview(showBackground = true, backgroundColor = 0xFF101922)
@Composable
fun PreviewHelpSupportScreen() {
    HelpAndSupportScreen(
        onBackClick = {},
        onTermsClick = {},
        onPrivacyClick = {}
    )
}