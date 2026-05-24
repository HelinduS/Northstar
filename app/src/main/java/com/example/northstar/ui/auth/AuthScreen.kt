package com.example.northstar.ui.auth

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val currentScreen = rememberSaveable { mutableStateOf("Login") }
    val uiState by forgotPasswordViewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen.value, label = "AuthScreenAnimation") { screen ->
            when (screen) {
                "Login" -> LoginScreen(
                    onLoginSuccess = { onAuthSuccess("") },
                    onRegisterClick = { currentScreen.value = "Register" },
                    onForgotPasswordClick = { currentScreen.value = "ForgotPassword" }
                )

                "Register" -> RegisterScreen(
                    onRegisterSuccess = { currentScreen.value = "Login" },
                    onLoginClick = { currentScreen.value = "Login" }
                )

                "ForgotPassword" -> ForgotPasswordScreen(
                    viewModel = forgotPasswordViewModel,
                    onOtpSent = { currentScreen.value = "Verification" },
                    onBackToLoginClick = { currentScreen.value = "Login" }
                )

                "Verification" -> VerificationScreen(
                    viewModel = forgotPasswordViewModel,
                    onVerified = { currentScreen.value = "ResetSuccess" },
                    onBackClick = { currentScreen.value = "ForgotPassword" }
                )

                // ResetPasswordScreen is removed — password change happens
                // via the Firebase link the user receives after OTP verification.
                "ResetSuccess" -> ResetSuccessScreen(
                    email = uiState.email,
                    onBackToLogin = { currentScreen.value = "Login" }
                )
            }
        }
    }
}