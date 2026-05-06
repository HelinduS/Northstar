package com.example.northstar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.northstar.ui.components.AppTextField
import com.example.northstar.ui.components.PrimaryButton

@Composable
fun VerificationScreen(
    onVerifyClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpState by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

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

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Verification Code",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the 4-digit code sent to your registered contact method.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = otpState,
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }.take(4)
                otpState = filtered
                otpError = false
            },
            label = "Verification Code",
            isError = otpError,
            supportingText = "Must be 4 digits",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Verify Code",
            onClick = {
                val cleanText = otpState.filter { it.isDigit() }
                if (cleanText.length != 4) {
                    otpError = true
                } else {
                    onVerifyClick(cleanText)
                }
            }
        )
    }
}