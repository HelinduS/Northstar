package com.example.northstar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.northstar.ui.components.AppPasswordTextField
import com.example.northstar.ui.components.AppTextField
import com.example.northstar.ui.components.PrimaryButton

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val nameError = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign up to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))


        AppTextField(
            value = nameState.value,
            onValueChange = {
                nameState.value = it
                nameError.value = false
            },
            label = "Full Name",
            isError = nameError.value,
            supportingText = "Name cannot be empty",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        AppTextField(
            value = emailState.value,
            onValueChange = {
                emailState.value = it
                emailError.value = false
            },
            label = "Email Address",
            isError = emailError.value,
            supportingText = "Please enter a valid email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        AppPasswordTextField(
            value = passwordState.value,
            onValueChange = {
                passwordState.value = it
                passwordError.value = false
            },
            label = "Password",
            isPasswordVisible = passwordVisible.value,
            onVisibilityToggle = { passwordVisible.value = !passwordVisible.value },
            isError = passwordError.value,
            supportingText = "Password cannot be empty",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))



        PrimaryButton(
            text = "Sign Up",
            onClick = {
                var valid = true
                if (nameState.value.isBlank()) {
                    nameError.value = true
                    valid = false
                }
                if (emailState.value.isBlank()) {
                    emailError.value = true
                    valid = false
                }
                if (passwordState.value.isBlank()) {
                    passwordError.value = true
                    valid = false
                }

                if (valid) {
                    onRegisterClick(nameState.value, emailState.value, passwordState.value)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}