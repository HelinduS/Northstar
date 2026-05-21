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
fun TermsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Terms of Service",
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
            Text(
                text = "Effective date: May 2026",
                fontSize = 12.sp,
                color = TextMuted,
                fontFamily = InterFontFamily
            )

            TermsSection(
                title = "1. Acceptance of Terms",
                content = "By downloading, installing, or using NorthStar, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use the application. These terms apply to all users of the NorthStar personal finance management application."
            )

            TermsSection(
                title = "2. Use of the Application",
                content = "NorthStar is provided for personal, non-commercial use only. You agree to use the application only for lawful purposes and in a manner that does not infringe the rights of others. You are responsible for maintaining the confidentiality of your account credentials."
            )

            TermsSection(
                title = "3. User Account",
                content = "You must create an account to use NorthStar. You are responsible for all activities that occur under your account. You must provide accurate and complete information when creating your account and keep your information up to date. You must be at least 18 years old to create an account."
            )

            TermsSection(
                title = "4. Financial Data Accuracy",
                content = "NorthStar is a manual data entry application. We do not guarantee the accuracy, completeness, or reliability of any financial calculations or projections displayed. The application is intended as a personal finance tracking tool only and should not be used as the sole basis for financial decisions."
            )

            TermsSection(
                title = "5. Intellectual Property",
                content = "All content, features, and functionality of NorthStar, including but not limited to the design, code, graphics, and user interface, are owned by the NorthStar development team and are protected by applicable intellectual property laws. You may not copy, modify, or distribute any part of the application without prior written consent."
            )

            TermsSection(
                title = "6. Disclaimer of Warranties",
                content = "NorthStar is provided on an 'as is' and 'as available' basis without any warranties of any kind, either express or implied. We do not warrant that the application will be uninterrupted, error-free, or free of viruses or other harmful components. Use of the application is at your own risk."
            )

            TermsSection(
                title = "7. Limitation of Liability",
                content = "To the maximum extent permitted by law, the NorthStar development team shall not be liable for any indirect, incidental, special, or consequential damages arising from your use of the application, including but not limited to loss of data, financial loss, or business interruption."
            )

            TermsSection(
                title = "8. Termination",
                content = "We reserve the right to suspend or terminate your account at any time if you violate these Terms of Service. Upon termination, your right to use the application will immediately cease. You may also terminate your account at any time through the app settings."
            )

            TermsSection(
                title = "9. Changes to Terms",
                content = "We reserve the right to modify these Terms of Service at any time. We will notify users of significant changes through the application. Your continued use of NorthStar after changes are posted constitutes your acceptance of the modified terms."
            )

            TermsSection(
                title = "10. Contact",
                content = "If you have any questions about these Terms of Service, please contact us at support@northstar.app. We are committed to resolving any concerns promptly and fairly."
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
private fun TermsSection(title: String, content: String) {
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