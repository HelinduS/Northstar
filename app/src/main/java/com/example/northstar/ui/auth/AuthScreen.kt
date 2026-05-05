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
    // State to keep track of which screen to show. True = Login, False = Register
    val isLoginScreen = remember { mutableStateOf(true) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Crossfade adds a smooth animation when switching between screens
        Crossfade(targetState = isLoginScreen.value, label = "AuthScreenAnimation") { showLogin ->
            if (showLogin) {
                LoginScreen(
                    onLoginClick = { email, password ->
                        // When they log in successfully, tell the main app
                        onAuthSuccess(email)
                    },
                    onRegisterClick = {
                        // Switch the state to show the Register screen
                        isLoginScreen.value = false
                    }
                )
            } else {
                RegisterScreen(
                    onRegisterClick = { name, email, password ->
                        // After they sign up, switch back to the Login screen
                        isLoginScreen.value = true
                    },
                    onLoginClick = {
                        // Switch the state back to show the Login screen
                        isLoginScreen.value = true
                    }
                )
            }
        }
    }
}