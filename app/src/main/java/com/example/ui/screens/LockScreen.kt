package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.IndigoSecondary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LockScreen(
    isPinSet: Boolean,
    appTitle: String = "Öztürk Ailesi",
    onUnlock: (String) -> Boolean,
    onSetupPin: (String, String, String) -> Boolean,
    onResetPinWithAnswer: (String, String) -> Boolean,
    securityQuestion: String,
    onQuickUnlock: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSettingUp by remember { mutableStateOf(!isPinSet) }

    // Setup state
    var setupStep by remember { mutableStateOf(1) } // 1: enter pin, 2: confirm pin, 3: security question
    var firstEnteredPin by remember { mutableStateOf("") }
    var selectedQuestion by remember { mutableStateOf("En sevdiğin şehir neresi?") }
    var securityAnswer by remember { mutableStateOf("") }

    // Forgot PIN dialog
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotAnswer by remember { mutableStateOf("") }
    var newResetPin by remember { mutableStateOf("") }
    var forgotError by remember { mutableStateOf<String?>(null) }

    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun triggerShake() {
        scope.launch {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -10f at 250
                    10f at 300
                    0f at 400
                }
            )
        }
    }

    LaunchedEffect(enteredPin) {
        if (enteredPin.length == 4) {
            if (!isSettingUp) {
                val success = onUnlock(enteredPin)
                if (!success) {
                    errorMessage = "Hatalı PIN! Lütfen tekrar deneyin."
                    triggerShake()
                    enteredPin = ""
                }
            } else {
                if (setupStep == 1) {
                    firstEnteredPin = enteredPin
                    enteredPin = ""
                    setupStep = 2
                } else if (setupStep == 2) {
                    if (enteredPin == firstEnteredPin) {
                        enteredPin = ""
                        setupStep = 3
                    } else {
                        errorMessage = "PIN eşleşmedi! Baştan deneyin."
                        triggerShake()
                        enteredPin = ""
                        setupStep = 1
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Subtle background texture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                            AmberContainer.copy(alpha = 0.2f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // App Logo / Lock Icon with 4-person hugging family
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(AmberContainer)
                    .border(2.5.dp, AmberPrimary.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSettingUp) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Güvenlik Kilidi",
                        tint = AmberOnContainer,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.family_hug_logo_1787295431002),
                        contentDescription = "Öztürk Ailesi Logosu",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isSettingUp) {
                    when (setupStep) {
                        1 -> "Yeni PIN Belirleyin"
                        2 -> "PIN'i Doğrulayın"
                        else -> "Kurtarma Sorusunu Seçin"
                    }
                } else {
                    appTitle
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isSettingUp) {
                    when (setupStep) {
                        1 -> "Anılarınıza yalnızca sizin erişmeniz için 4 haneli PIN girin"
                        2 -> "Lütfen belirlediğiniz 4 haneli PIN'i tekrar girin"
                        else -> "PIN unutulduğunda kurtarmak için güvenlik sorusu belirleyin"
                    }
                } else {
                    "Bu anı günlüğü özel izinle kilitlenmiştir. Giriş yapmak için PIN giriniz."
                },
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isSettingUp && setupStep == 3) {
                // Step 3: Security Question input
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Güvenlik Sorusu",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = selectedQuestion,
                            onValueChange = { selectedQuestion = it },
                            label = { Text("Kurtarma Sorusu") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = securityAnswer,
                            onValueChange = { securityAnswer = it },
                            label = { Text("Cevabınız") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (securityAnswer.isNotBlank()) {
                                    onSetupPin(firstEnteredPin, selectedQuestion, securityAnswer)
                                }
                            },
                            enabled = securityAnswer.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_complete_setup")
                        ) {
                            Text("Şifrelemeyi Tamamla ve Giriş Yap", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // PIN Dots Display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                        .padding(bottom = 16.dp)
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) AmberPrimary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) AmberPrimary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Numeric Keypad (3x4)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("bio", "0", "del")
                    )

                    for (row in rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (item in row) {
                                when (item) {
                                    "del" -> {
                                        Surface(
                                            onClick = {
                                                if (enteredPin.isNotEmpty()) {
                                                    enteredPin = enteredPin.dropLast(1)
                                                    errorMessage = null
                                                }
                                            },
                                            shape = CircleShape,
                                            color = Color.Transparent,
                                            modifier = Modifier
                                                .size(68.dp)
                                                .testTag("keypad_del")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Backspace,
                                                    contentDescription = "Sil",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                    "bio" -> {
                                        Surface(
                                            onClick = {
                                                onQuickUnlock()
                                            },
                                            shape = CircleShape,
                                            color = AmberContainer.copy(alpha = 0.5f),
                                            modifier = Modifier
                                                .size(68.dp)
                                                .testTag("keypad_bio")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Fingerprint,
                                                    contentDescription = "Biyometrik / Hızlı Doğrulama",
                                                    tint = AmberOnContainer,
                                                    modifier = Modifier.size(30.dp)
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        Surface(
                                            onClick = {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += item
                                                    errorMessage = null
                                                }
                                            },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surface,
                                            shadowElevation = 2.dp,
                                            modifier = Modifier
                                                .size(68.dp)
                                                .testTag("keypad_$item")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = item,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!isSettingUp) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { showForgotDialog = true },
                        modifier = Modifier.testTag("btn_forgot_pin")
                    ) {
                        Text(
                            text = "PIN Kodumu Unuttum",
                            color = IndigoSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Forgot PIN Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PIN Sıfırlama")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Güvenlik Sorusu:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = securityQuestion,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotAnswer,
                        onValueChange = {
                            forgotAnswer = it
                            forgotError = null
                        },
                        label = { Text("Cevabınız") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newResetPin,
                        onValueChange = {
                            if (it.length <= 4) newResetPin = it
                            forgotError = null
                        },
                        label = { Text("Yeni 4 Haneli PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (forgotError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = forgotError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotAnswer.isBlank() || newResetPin.length < 4) {
                            forgotError = "Lütfen cevabı ve 4 haneli yeni PIN'i giriniz."
                        } else {
                            val success = onResetPinWithAnswer(newResetPin, forgotAnswer)
                            if (success) {
                                showForgotDialog = false
                            } else {
                                forgotError = "Yanlış güvenlik cevabı!"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                ) {
                    Text("PIN'i Sıfırla ve Aç")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}
