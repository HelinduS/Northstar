package com.example.northstar.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Privacy Policy",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy900
                )
            )
        },
        containerColor = Surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Last updated
            Text(
                text = "Last updated: May 2026",
                fontSize = 12.sp,
                color = TextMuted,
                fontFamily = InterFontFamily
            )

            PolicySection(
                title = "1. Information We Collect",
                content = "NorthStar collects information you provide directly to us when you create an account, including your name, email address, and financial data such as income entries, expense records, and savings goals. We do not collect any bank account credentials or payment card information."
            )

            PolicySection(
                title = "2. How We Use Your Information",
                content = "We use the information we collect to provide, maintain, and improve the NorthStar application. Your financial data is used solely to display your personal finance summary, generate reports, and track your savings goals. We do not sell, trade, or share your personal information with third parties."
            )

            PolicySection(
                title = "3. Data Storage & Security",
                content = "Your data is stored securely using Google Firebase Firestore. All data is encrypted in transit using industry-standard TLS encryption. Access to your data is protected by Firebase Authentication and Security Rules that ensure only you can read or write your own financial records."
            )

            PolicySection(
                title = "4. Data Retention",
                content = "We retain your personal data for as long as your account is active. You may request deletion of your account and all associated data at any time through the app settings. Upon deletion, all your financial records will be permanently removed from our servers within 30 days."
            )

            PolicySection(
                title = "5. Third-Party Services",
                content = "NorthStar uses the following third-party services: Google Firebase (authentication and database), Google AdSense data is manually entered by users and not automatically synced. We are not responsible for the privacy practices of these third-party services."
            )

            PolicySection(
                title = "6. Children's Privacy",
                content = "NorthStar is not intended for use by individuals under the age of 18. We do not knowingly collect personal information from children. If you believe a child has provided us with personal information, please contact us immediately."
            )

            PolicySection(
                title = "7. Changes to This Policy",
                content = "We may update this Privacy Policy from time to time. We will notify you of any changes by updating the date at the top of this policy. Your continued use of the application after any changes constitutes your acceptance of the new policy."
            )

            PolicySection(
                title = "8. Contact Us",
                content = "If you have any questions about this Privacy Policy or our data practices, please contact us at support@northstar.app. We will respond to all inquiries within 5 business days."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "NorthStar · Personal Finance Management",
                fontSize = 11.sp,
                color = TextHint,
                fontFamily = InterFontFamily,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900,
                fontFamily = InterFontFamily
            )
            Text(
                text = content,
                fontSize = 13.sp,
                color = TextSecondary,
                fontFamily = InterFontFamily,
                lineHeight = 20.sp
            )
        }
    }
}