package com.example.northstar.ui.auth

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Keeps track of the active screen in the auth flow
    val currentScreen = remember { mutableStateOf("Login") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen.value, label = "AuthScreenAnimation") { screen ->
            when (screen) {
                "Login" -> {
                    LoginScreen(
                        onLoginClick = { email, password -> onAuthSuccess(email) },
                        onRegisterClick = { currentScreen.value = "Register" },
                        // Now routing to the initial forgot password entry
                        onForgotPasswordClick = { currentScreen.value = "ForgotPassword" }
                    )
                }
                "Register" -> {
                    RegisterScreen(
                        onRegisterClick = { _, _, _ -> currentScreen.value = "Login" },
                        onLoginClick = { currentScreen.value = "Login" }
                    )
                }
                // --- NEW SCREENS FOR THE RESET SCENARIO ---
                "ForgotPassword" -> {
                    // Scenario Screen 1: Request Email/SMS (matches image_8.png)
                    ForgotPasswordScreen(
                        onSendInstructionsClick = { contactDetail ->
                            // TODO: Add logic to determine if it's email/SMS.
                            // For now, route straight to verification
                            currentScreen.value = "Verification"
                        },
                        onBackToLoginClick = { currentScreen.value = "Login" }
                    )
                }
                "Verification" -> {
                    // Scenario Screen 2: OTP Entry (matches image_9.png)
                    VerificationScreen(
                        onVerifyClick = { otpCode ->
                            // Route to reset if code is accepted
                            currentScreen.value = "ResetPassword"
                        },
                        onBackClick = { currentScreen.value = "ForgotPassword" }
                    )
                }
                "ResetPassword" -> {
                    // Scenario Screen 3: New Password (matches image_10.png)
                    ResetPasswordScreen(
                        onResetClick = { newPassword ->
                            currentScreen.value = "ResetSuccess"
                        },
                        onBackClick = { currentScreen.value = "Verification" }
                    )
                }
                "ResetSuccess" -> {
                    // Scenario Screen 4: Success Message (matches image_11.png)
                    ResetSuccessScreen(
                        onContinueClick = { currentScreen.value = "Login" }
                    )
                }
            }
        }
    }
}