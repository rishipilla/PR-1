package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanDark
import com.example.ui.theme.PrayogaError
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaSaffron
import com.example.ui.theme.PrayogaSuccess
import com.example.ui.theme.PrayogaTeal
import com.example.ui.theme.PrayogaTextMuted
import com.example.ui.theme.PrayogaTextPrimary
import com.example.ui.theme.PrayogaTextSecondary
import com.example.viewmodel.PrayogaViewModel

@Composable
fun EvidenceScreen(
    viewModel: PrayogaViewModel,
    modifier: Modifier = Modifier
) {
    val modelEvidence by viewModel.modelEvidence.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val latestRecognition by viewModel.latestRecognition.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MODEL EVIDENCE & GATE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = PrayogaCyanDark
                )
                Text(
                    text = "Verification, Metrics & Confidence Gating",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrayogaTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrayogaSuccess.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = PrayogaSuccess, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("VERIFIED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrayogaSuccess)
                }
            }
        }

        // ==========================================
        // MODEL PROVENANCE CARD
        // ==========================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = PrayogaCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Model Provenance & Integrity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrayogaTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Artifact SHA-256 Fingerprint:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrayogaTextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = modelEvidence.sha256,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = PrayogaTextPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(modelEvidence.accuracy * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PrayogaCyanDark)
                        Text("Accuracy", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(modelEvidence.macroF1 * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PrayogaTeal)
                        Text("Macro F1", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${modelEvidence.latencyMs} ms", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PrayogaSaffron)
                        Text("Inference", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${modelEvidence.featureCount}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PrayogaCyanDark)
                        Text("Features", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                }
            }
        }

        // ==========================================
        // CONFIDENCE GATE SLIDER
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Gate",
                            tint = PrayogaTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confidence Gate Threshold",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrayogaTextPrimary
                        )
                    }

                    Text(
                        text = "${(userSettings.confidenceThreshold * 100).toInt()}%",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = PrayogaTeal
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Low-confidence predictions below this threshold are rejected from being spoken to prevent incorrect utterances.",
                    fontSize = 12.sp,
                    color = PrayogaTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = userSettings.confidenceThreshold,
                    onValueChange = { viewModel.updateConfidenceThreshold(it) },
                    valueRange = 0.70f..0.98f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrayogaTeal,
                        activeTrackColor = PrayogaTeal,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    )
                )

                // Accept vs Reject preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PrayogaSuccess))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(">= ${(userSettings.confidenceThreshold * 100).toInt()}% -> ACCEPT (SPEAK)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrayogaSuccess)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PrayogaError))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("< Threshold -> REJECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrayogaError)
                    }
                }
            }
        }

        // ==========================================
        // SYSTEM ARCHITECTURE 9-STEP PIPELINE
        // ==========================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "End-to-End Recognition Architecture",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PrayogaTextPrimary
                )
                Text(
                    text = "Hardware Acquisition → Local ML Pipeline → Spoken Output",
                    fontSize = 12.sp,
                    color = PrayogaTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                val steps = listOf(
                    "1. USER" to "Silent articulation of intentional phrase",
                    "2. WEARABLE HEADSET" to "MPU6050 (6-DOF) + Conditioned Piezo Sensor",
                    "3. ESP32 BLE LINK" to "19-byte packet format @ 100 Hz sampling",
                    "4. PACKET DECODER" to "Magic 0xA5 + XOR checksum + Gap detection",
                    "5. 300-SAMPLE BUFFER" to "~3.0-second rolling articulation window",
                    "6. PREPROCESSING" to "Baseline centering, zero-crossing, energy envelope",
                    "7. LOCAL ML INFERENCE" to "Classifies 5 base + custom voice datasets",
                    "8. CONFIDENCE GATE" to "Enforces threshold (Reject if uncertain)",
                    "9. AUDIO OUTPUT" to "Speaks validated phrase via Phone Speaker"
                )

                steps.forEachIndexed { index, (title, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE0F2FE),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrayogaCyanDark
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrayogaTextPrimary
                            )
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = PrayogaTextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
