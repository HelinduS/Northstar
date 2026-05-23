package com.example.northstar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.northstar.ui.components.AppTextField
import com.example.northstar.ui.components.PrimaryButton
import com.example.northstar.ui.theme.Debit
import com.example.northstar.ui.theme.TextMuted

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    val firstNameError = remember { mutableStateOf(false) }
    val lastNameError = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    val phoneError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val confirmPasswordError = remember { mutableStateOf(false) }
    var confirmPasswordMismatch by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
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

            // First Name
            AppTextField(
                value = firstNameState.value,
                onValueChange = {
                    firstNameState.value = it
                    firstNameError.value = false
                },
                label = "First Name",
                isError = firstNameError.value,
                supportingText = "First name cannot be empty",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last Name
            AppTextField(
                value = lastNameState.value,
                onValueChange = {
                    lastNameState.value = it
                    lastNameError.value = false
                },
                label = "Last Name",
                isError = lastNameError.value,
                supportingText = "Last name cannot be empty",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
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

            // Phone Number
            AppTextField(
                value = phoneState.value,
                onValueChange = {
                    phoneState.value = it
                    phoneError.value = false
                },
                label = "Phone Number",
                isError = phoneError.value,
                supportingText = "Please enter a valid phone number",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            AppTextField(
                value = addressState.value,
                onValueChange = { addressState.value = it },
                label = "Address (optional)",
                isError = false,
                supportingText = "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            NorthStarInputField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                    passwordError.value = false
                    confirmPasswordMismatch = false
                },
                label = "Password",
                isError = passwordError.value,
                errorText = "Password must be at least 6 characters",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (passwordError.value) Debit else TextMuted
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }) {
                        Icon(
                            if (passwordVisible.value) Icons.Outlined.VisibilityOff
                            else Icons.Outlined.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = TextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible.value)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            NorthStarInputField(
                value = confirmPasswordState.value,
                onValueChange = {
                    confirmPasswordState.value = it
                    confirmPasswordError.value = false
                    confirmPasswordMismatch = false
                },
                label = "Confirm Password",
                isError = confirmPasswordError.value || confirmPasswordMismatch,
                errorText = if (confirmPasswordMismatch)
                    "Passwords do not match"
                else
                    "Please confirm your password",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (confirmPasswordError.value || confirmPasswordMismatch)
                            Debit else TextMuted
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        confirmPasswordVisible.value = !confirmPasswordVisible.value
                    }) {
                        Icon(
                            if (confirmPasswordVisible.value) Icons.Outlined.VisibilityOff
                            else Icons.Outlined.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = TextMuted
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible.value)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            if (uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            PrimaryButton(
                text = if (uiState is AuthUiState.Loading)
                    "Creating Account..." else "Sign Up",
                enabled = uiState !is AuthUiState.Loading,
                onClick = {
                    var valid = true

                    if (firstNameState.value.isBlank()) {
                        firstNameError.value = true
                        valid = false
                    }
                    if (lastNameState.value.isBlank()) {
                        lastNameError.value = true
                        valid = false
                    }
                    if (emailState.value.isBlank()) {
                        emailError.value = true
                        valid = false
                    }
                    if (phoneState.value.isBlank()) {
                        phoneError.value = true
                        valid = false
                    }
                    if (passwordState.value.length < 6) {
                        passwordError.value = true
                        valid = false
                    }
                    if (confirmPasswordState.value != passwordState.value) {
                        confirmPasswordMismatch = true
                        valid = false
                    }

                    if (valid) {
                        val fullName = "${firstNameState.value.trim()} ${lastNameState.value.trim()}"
                        viewModel.register(
                            email = emailState.value.trim(),
                            password = passwordState.value,
                            displayName = fullName,
                            phone = phoneState.value.trim(),
                            address = addressState.value.trim()
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Log In",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (uiState is AuthUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}