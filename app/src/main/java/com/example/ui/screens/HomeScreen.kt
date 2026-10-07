package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeadsetState
import com.example.ui.components.GlassCard
import com.example.ui.components.HeadsetStatusPill
import com.example.ui.components.LiveSensorWaveformCanvas
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanDark
import com.example.ui.theme.PrayogaCyanLight
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassCard
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaSaffron
import com.example.ui.theme.PrayogaSuccess
import com.example.ui.theme.PrayogaTeal
import com.example.ui.theme.PrayogaTextMuted
import com.example.ui.theme.PrayogaTextPrimary
import com.example.ui.theme.PrayogaTextSecondary
import com.example.viewmodel.PrayogaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: PrayogaViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToDatasets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headsetState by viewModel.headsetState.collectAsState()
    val device by viewModel.device.collectAsState()
    val vocabulary by viewModel.vocabulary.collectAsState()
    val bufferFill by viewModel.bufferFill.collectAsState()
    val recentSamples by viewModel.recentSamples.collectAsState()
    val latestRecognition by viewModel.latestRecognition.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_button")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header with identity and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROJECT PRAYOGA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = PrayogaCyanDark
                )
                Text(
                    text = "Silent Articulation Interface",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrayogaTextSecondary
                )
            }
            HeadsetStatusPill(state = headsetState)
        }

        // ==========================================
        // PROMINENT HERO: READY TO SCAN
        // ==========================================
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_scan_card"),
            shapeRadius = 24.dp,
            elevation = 8.dp,
            backgroundColor = Color.White.copy(alpha = 0.92f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Visual badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE0F2FE),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrayogaCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (device.isConnected) PrayogaSuccess else PrayogaCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (device.isConnected) "READY TO SCAN & ARTICULATE" else "READY TO SCAN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = PrayogaCyanDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pair Wearable Headset",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrayogaTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Scan the QR code on your ESP32 headset to initiate low-latency BLE synchronization and 100 Hz sensor telemetry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrayogaTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Prominent glowing scanner button
                Button(
                    onClick = onNavigateToScanner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(pulseScale)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = PrayogaCyan
                        )
                        .testTag("prominent_scan_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrayogaCyan,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Headset QR",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "START CAMERA SCANNER",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Small Headset info chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bluetooth,
                        contentDescription = "Device",
                        tint = PrayogaCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Target Device: ${device.deviceId} (ESP32) • 100 Hz",
                        fontSize = 12.sp,
                        color = PrayogaTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ==========================================
        // SILENT ARTICULATION TRIGGER SECTION
        // ==========================================
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("articulation_card"),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCCFBF1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = PrayogaTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Silent Articulation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PrayogaTextPrimary
                            )
                            Text(
                                text = "300-Sample Recognition Window (3.0s)",
                                fontSize = 12.sp,
                                color = PrayogaTextSecondary
                            )
                        }
                    }

                    if (isSpeaking) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrayogaTeal.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speaking",
                                    tint = PrayogaTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Speaking...",
                                    fontSize = 11.sp,
                                    color = PrayogaTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time sensor preview waveform
                Text(
                    text = "Live Telemetry: Piezo (Cyan) & Accel (Saffron)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrayogaTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LiveSensorWaveformCanvas(
                    samples = recentSamples,
                    height = 70.dp
                )

                if (headsetState == HeadsetState.LISTENING || headsetState == HeadsetState.PROCESSING) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (headsetState == HeadsetState.LISTENING) "Capturing Jaw/Throat Dynamics..." else "Running Feature Extraction & ML...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrayogaCyanDark
                            )
                            Text(
                                text = "${(bufferFill * 300).toInt()}/300 samples",
                                fontSize = 12.sp,
                                color = PrayogaTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { bufferFill },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrayogaCyan,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.triggerArticulationRecognition() },
                    enabled = headsetState != HeadsetState.LISTENING && headsetState != HeadsetState.PROCESSING,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("trigger_articulate_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrayogaTeal,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Articulate",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (headsetState == HeadsetState.LISTENING) "RECORDING ARTICULATION..." else "TAP TO ARTICULATE PHRASE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // ==========================================
        // VOCABULARY QUICK LIST
        // ==========================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Supported Vocabulary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrayogaTextPrimary
                        )
                        Text(
                            text = "Tap any phrase to test voice output",
                            fontSize = 12.sp,
                            color = PrayogaTextSecondary
                        )
                    }
                    Text(
                        text = "View All (${vocabulary.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrayogaCyan,
                        modifier = Modifier
                            .clickable { onNavigateToDatasets() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vocabulary.take(6).forEach { phrase ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.speakText(phrase.spokenText)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (phrase.isCustom) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (phrase.isCustom) PrayogaSaffron.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = if (phrase.isCustom) PrayogaSaffron else PrayogaCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = phrase.phrase,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrayogaTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // RECENT SPOKEN OUTPUT
        // ==========================================
        latestRecognition?.let { recognition ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 20.dp,
                borderColor = if (recognition.accepted) PrayogaSuccess else PrayogaSaffron
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (recognition.accepted) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = "Status",
                                tint = if (recognition.accepted) PrayogaSuccess else PrayogaSaffron,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (recognition.accepted) "Validated Spoken Output" else "Low Confidence Articulation",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (recognition.accepted) PrayogaSuccess else PrayogaSaffron
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${recognition.spokenText}\"",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrayogaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Confidence: ${(recognition.confidence * 100).toInt()}% • Latency: ${recognition.latencyMs}ms",
                            fontSize = 11.sp,
                            color = PrayogaTextMuted
                        )
                    }

                    Button(
                        onClick = { viewModel.speakText(recognition.spokenText) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0F2FE),
                            contentColor = PrayogaCyanDark
                        ),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Replay Audio",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
