package com.example.northstar.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*

@Composable
fun PinScreen(
    mode: PinMode,
    onSuccess: () -> Unit,
    pinLockManager: PinLockManager
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(White.copy(alpha = 0.1f))
                    .border(1.dp, White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "N★",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W800,
                    color = White,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = when {
                    mode == PinMode.SETUP && !isConfirming -> "Create a PIN"
                    mode == PinMode.SETUP && isConfirming  -> "Confirm your PIN"
                    else                                   -> "Enter your PIN"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.W800,
                color = White,
                letterSpacing = (-0.5).sp,
                fontFamily = InterFontFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    mode == PinMode.SETUP && !isConfirming -> "Choose a 4-digit PIN to secure the app"
                    mode == PinMode.SETUP && isConfirming  -> "Enter the same PIN again"
                    else                                   -> "Enter your PIN to continue"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = White.copy(alpha = 0.45f),
                fontFamily = InterFontFamily
            )

            Spacer(modifier = Modifier.height(40.dp))

            // PIN dots
            val currentPin = if (isConfirming) confirmPin else pin
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < currentPin.length) White
                                else White.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            // Error
            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Color(0xFFFF8787),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.W500
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Keypad
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "⌫")
            )

            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    row.forEach { key ->
                        PinKey(
                            key = key,
                            onClick = {
                                errorMessage = ""
                                when (key) {
                                    "⌫" -> {
                                        if (isConfirming) {
                                            if (confirmPin.isNotEmpty())
                                                confirmPin = confirmPin.dropLast(1)
                                        } else {
                                            if (pin.isNotEmpty())
                                                pin = pin.dropLast(1)
                                        }
                                    }
                                    ""  -> { /* empty slot */ }
                                    else -> {
                                        if (isConfirming) {
                                            if (confirmPin.length < 4) {
                                                confirmPin += key
                                                if (confirmPin.length == 4) {
                                                    if (mode == PinMode.SETUP) {
                                                        if (confirmPin == pin) {
                                                            pinLockManager.savePin(pin)
                                                            onSuccess()
                                                        } else {
                                                            errorMessage = "PINs don't match. Try again."
                                                            pin = ""
                                                            confirmPin = ""
                                                            isConfirming = false
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            if (pin.length < 4) {
                                                pin += key
                                                if (pin.length == 4) {
                                                    if (mode == PinMode.UNLOCK) {
                                                        if (pinLockManager.verifyPin(pin)) {
                                                            pinLockManager.unlock()
                                                            onSuccess()
                                                        } else {
                                                            errorMessage = "Incorrect PIN. Try again."
                                                            pin = ""
                                                        }
                                                    } else {
                                                        // SETUP — move to confirm
                                                        isConfirming = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKey(key: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                if (key.isBlank()) Color.Transparent
                else White.copy(alpha = 0.08f)
            )
            .then(
                if (key.isNotBlank())
                    Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (key == "⌫") {
            Icon(
                Icons.Outlined.Backspace,
                contentDescription = "Delete",
                tint = White.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        } else if (key.isNotBlank()) {
            Text(
                text = key,
                fontSize = 24.sp,
                fontWeight = FontWeight.W500,
                color = White,
                fontFamily = InterFontFamily
            )
        }
    }
}

enum class PinMode { SETUP, UNLOCK }