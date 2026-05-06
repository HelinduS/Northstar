package com.example.northstar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.northstar.ui.components.AppPasswordTextField
import com.example.northstar.ui.components.PrimaryButton

@Composable
fun ResetPasswordScreen(
    onResetClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newPasswordState by remember { mutableStateOf("") }
    var confirmPasswordState by remember { mutableStateOf("") }

    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    var errorText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start)) {
            Text("< Back", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Reset your password here",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter and confirm your new password",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // First Password Input
        AppPasswordTextField(
            value = newPasswordState,
            onValueChange = {
                newPasswordState = it
                passwordError = false
            },
            label = "New Password",
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            isError = passwordError,
            supportingText = errorText.ifBlank { "Password cannot be empty" },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Input
        AppPasswordTextField(
            value = confirmPasswordState,
            onValueChange = {
                confirmPasswordState = it
                confirmPasswordError = false
                passwordError = false
            },
            label = "Confirm Password",
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { /* Uses the same toggle state */ },
            isError = confirmPasswordError,
            supportingText = errorText.ifBlank { "Please re-enter your password" },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Reset My Password",
            onClick = {
                when {
                    newPasswordState.isBlank() -> {
                        passwordError = true
                        errorText = "Password cannot be empty"
                    }
                    confirmPasswordState.isBlank() -> {
                        confirmPasswordError = true
                        errorText = "Password cannot be empty"
                    }
                    newPasswordState != confirmPasswordState -> {
                        passwordError = true
                        confirmPasswordError = true
                        errorText = "Passwords do not match"
                    }
                    else -> {
                        onResetClick(newPasswordState)
                    }
                }
            }
        )
    }
}