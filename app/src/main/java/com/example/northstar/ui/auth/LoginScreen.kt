package com.example.northstar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.northstar.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
    ) {

        // ═══════════════════════════════
        // 🔷 Enhanced Header Section
        // ═══════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp)
        ) {
            // Centered glow
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-40).dp)
                    .clip(RoundedCornerShape(200.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GreenDeep.copy(alpha = 0.32f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── "Now live" badge ──
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(White.copy(alpha = 0.07f))
                        .border(1.dp, White.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = "Now live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W600,
                            color = White.copy(alpha = 0.55f),
                            letterSpacing = 0.5.sp,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── Logo ──
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(White.copy(alpha = 0.10f))
                        .border(1.dp, White.copy(alpha = 0.16f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "N★",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.W900,
                        color = White,
                        fontFamily = InterFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── Title ──
                Text(
                    text = "Welcome back",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.W900,
                    color = White,
                    letterSpacing = (-1.2).sp,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Subtitle ──
                Text(
                    text = "Sign in to continue your journey",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W400,
                    color = White.copy(alpha = 0.50f),
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Secure / Fast / Reliable pills ──
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("\uD83D\uDCCA Track", "\uD83C\uDFAF Goals", "\uD83D\uDCB0 Save").forEach { label ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(White.copy(alpha = 0.06f))
                                .border(1.dp, White.copy(alpha = 0.10f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.W500,
                                color = White.copy(alpha = 0.45f),
                                fontFamily = InterFontFamily,

                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── "sign in below" divider ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = White.copy(alpha = 0.08f)
                    )
                    Text(
                        text = "sign in below",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W500,
                        color = White.copy(alpha = 0.22f),
                        fontFamily = InterFontFamily
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = White.copy(alpha = 0.08f)
                    )
                }
            }
        }

        // ═══════════════════════════════
        // 🟢 Bottom Sheet Section
        // ═══════════════════════════════
        val cs = MaterialTheme.colorScheme

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(cs.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Email field ──
                NorthStarInputField(
                    value = emailState.value,
                    onValueChange = {
                        emailState.value = it
                        emailError.value = false
                    },
                    label = "Email address",
                    isError = emailError.value,
                    errorText = "Please enter a valid email",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (emailError.value) Debit else cs.onSurfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Password field ──
                NorthStarInputField(
                    value = passwordState.value,
                    onValueChange = {
                        passwordState.value = it
                        passwordError.value = false
                    },
                    label = "Password",
                    isError = passwordError.value,
                    errorText = "Password cannot be empty",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (passwordError.value) Debit else cs.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisible.value = !passwordVisible.value
                        }) {
                            Icon(
                                if (passwordVisible.value)
                                    Icons.Outlined.VisibilityOff
                                else
                                    Icons.Outlined.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = cs.onSurfaceVariant
                            )
                        }
                    },
                    visualTransformation =
                        if (passwordVisible.value)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                // ── Forgot password ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onForgotPasswordClick) {
                        Text(
                            "Forgot password?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                            color = cs.primary,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                // ── Error message ──
                if (uiState is AuthUiState.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Debit.copy(alpha = 0.08f))
                            .border(1.dp, Debit.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = Debit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Login button ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (uiState is AuthUiState.Loading)
                                GreenDeep.copy(alpha = 0.5f)
                            else
                                GreenDeep
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Button(
                            onClick = {
                                var valid = true
                                if (emailState.value.isBlank()) {
                                    emailError.value = true
                                    valid = false
                                }
                                if (passwordState.value.isBlank()) {
                                    passwordError.value = true
                                    valid = false
                                }
                                if (valid) {
                                    viewModel.login(
                                        emailState.value,
                                        passwordState.value
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = White
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                "Log In",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.W700,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ── Sign up link ──
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Don't have an account?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W400,
                        color = cs.onSurfaceVariant,
                        fontFamily = InterFontFamily
                    )
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            "Sign Up",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W700,
                            color = cs.primary,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        }
    }
}

// ── Reusable input field ──
@Composable
fun NorthStarInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val cs = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label,
                    fontSize = 13.sp,
                    fontFamily = InterFontFamily
                )
            },
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = cs.primary,
                unfocusedBorderColor = cs.outline,
                errorBorderColor = Debit,
                focusedLabelColor = cs.primary,
                unfocusedLabelColor = cs.onSurfaceVariant,
                errorLabelColor = Debit,
                cursorColor = cs.primary,
                focusedTextColor = cs.onSurface,
                unfocusedTextColor = cs.onSurface,
                errorTextColor = cs.onSurface,
                focusedContainerColor = cs.surface,
                unfocusedContainerColor = cs.surface,
                errorContainerColor = cs.surface
            )
        )
        if (isError && errorText.isNotBlank()) {
            Text(
                errorText,
                fontSize = 11.sp,
                color = Debit,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
            )
        }
    }
}