package com.example.northstar.ui.auth

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScreen = rememberSaveable { mutableStateOf("Login") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen.value, label = "AuthScreenAnimation") { screen ->
            when (screen) {
                "Login" -> {
                    LoginScreen(
                        onLoginClick = { email, _ -> onAuthSuccess(email) },
                        onRegisterClick = { currentScreen.value = "Register" },
                        onForgotPasswordClick = { currentScreen.value = "ForgotPassword" }
                    )
                }
                "Register" -> {
                    RegisterScreen(
                        onRegisterClick = { _, _, _ -> currentScreen.value = "Login" },
                        onLoginClick = { currentScreen.value = "Login" }
                    )
                }
                "ForgotPassword" -> {
                    ForgotPasswordScreen(
                        onSendInstructionsClick = { _ ->
                            currentScreen.value = "Verification"
                        },
                        onBackToLoginClick = { currentScreen.value = "Login" }
                    )
                }
                "Verification" -> {
                    VerificationScreen(
                        onVerifyClick = { _ ->
                            currentScreen.value = "ResetPassword"
                        },
                        onBackClick = { currentScreen.value = "ForgotPassword" }
                    )
                }
                "ResetPassword" -> {
                    ResetPasswordScreen(
                        onResetClick = { _ ->
                            currentScreen.value = "ResetSuccess"
                        },
                        onBackClick = { currentScreen.value = "Verification" }
                    )
                }
                "ResetSuccess" -> {
                    ResetSuccessScreen(
                        onContinueClick = { currentScreen.value = "Login" }
                    )
                }
            }
        }
    }
}