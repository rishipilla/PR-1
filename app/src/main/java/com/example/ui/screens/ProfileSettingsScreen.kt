package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanDark
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaSaffron
import com.example.ui.theme.PrayogaSuccess
import com.example.ui.theme.PrayogaTeal
import com.example.ui.theme.PrayogaTextPrimary
import com.example.ui.theme.PrayogaTextSecondary
import com.example.viewmodel.PrayogaViewModel

@Composable
fun ProfileSettingsScreen(
    viewModel: PrayogaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val device by viewModel.device.collectAsState()

    var userNameInput by remember(userSettings.userName) { mutableStateOf(userSettings.userName) }
    var isEditingName by remember { mutableStateOf(false) }

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
                    text = "PROFILE & SETTINGS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = PrayogaCyanDark
                )
                Text(
                    text = "Speech Synthesis, Hardware & Safety",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrayogaTextSecondary
                )
            }
        }

        // ==========================================
        // USER PROFILE CARD
        // ==========================================
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_profile_card"),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = PrayogaCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        if (isEditingName) {
                            OutlinedTextField(
                                value = userNameInput,
                                onValueChange = { userNameInput = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = userSettings.userName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PrayogaTextPrimary
                            )
                            Text(
                                text = "${userSettings.userRole} • Articulation Ready",
                                fontSize = 12.sp,
                                color = PrayogaTextSecondary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (isEditingName) {
                                    viewModel.updateUserName(userNameInput)
                                    isEditingName = false
                                } else {
                                    isEditingName = true
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFFE0F2FE)
                    ) {
                        Text(
                            text = if (isEditingName) "Save" else "Edit",
                            color = PrayogaCyanDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Calibration info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Calibration", tint = PrayogaSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mandibular Calibration", fontSize = 12.sp, color = PrayogaTextPrimary, fontWeight = FontWeight.Medium)
                    }
                    Text("98.4% Matched", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrayogaSuccess)
                }
            }
        }

        // ==========================================
        // AUDIO & SPEECH SYNTHESIS SETTINGS
        // ==========================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Audio", tint = PrayogaCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voice Output & Pitch Controls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrayogaTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Phone Speaker Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Phone Speaker Output", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrayogaTextPrimary)
                        Text("Speak recognized silent phrases through phone", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Switch(
                        checked = userSettings.phoneSpeakerEnabled,
                        onCheckedChange = {
                            viewModel.updateSpeakerRouting(it, userSettings.headsetSpeakerEnabled)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrayogaCyan)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Headset Speaker Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Wearable Headset Speaker", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrayogaTextPrimary)
                        Text("Route audio stream back to ESP32 speaker", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Switch(
                        checked = userSettings.headsetSpeakerEnabled,
                        onCheckedChange = {
                            viewModel.updateSpeakerRouting(userSettings.phoneSpeakerEnabled, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrayogaCyan)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Voice Pitch Slider
                Text("Voice Pitch: ${String.format("%.1fx", userSettings.voicePitch)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrayogaTextPrimary)
                Slider(
                    value = userSettings.voicePitch,
                    onValueChange = { viewModel.updateVoicePitch(it) },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = PrayogaCyan, activeTrackColor = PrayogaCyan)
                )

                // Speech Rate Slider
                Text("Speech Rate: ${String.format("%.1fx", userSettings.speechRate)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrayogaTextPrimary)
                Slider(
                    value = userSettings.speechRate,
                    onValueChange = { viewModel.updateSpeechRate(it) },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = PrayogaCyan, activeTrackColor = PrayogaCyan)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        viewModel.speakText("Project Prayoga silent communication speech test.")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2FE), contentColor = PrayogaCyanDark)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Test Voice", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Voice Synthesis Aloud", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ==========================================
        // HARDWARE & HAPTICS SETTINGS
        // ==========================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = PrayogaTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hardware & Feedback",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrayogaTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Haptic Feedback", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrayogaTextPrimary)
                        Text("Vibrate phone when phrase is accepted", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Switch(
                        checked = userSettings.hapticFeedback,
                        onCheckedChange = { viewModel.updateHapticFeedback(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrayogaTeal)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("BLE Auto-Reconnect", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrayogaTextPrimary)
                        Text("Automatically resume stream when ESP32 in range", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Switch(
                        checked = userSettings.autoReconnect,
                        onCheckedChange = { },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrayogaTeal)
                    )
                }
            }
        }

        // ==========================================
        // ABOUT PROJECT PRAYOGA & SAFETY NOTICE
        // ==========================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shapeRadius = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "About", tint = PrayogaCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About Project Prayoga",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrayogaTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Project Prayoga is a wearable-to-phone silent communication system that detects subtle mechanical motion and vibrations associated with silent articulation, transmits sensor data over BLE, performs local ML recognition, and speaks the validated phrase through the phone speaker.",
                    fontSize = 12.sp,
                    color = PrayogaTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Safety and Product Boundaries as explicitly stated in the PDF
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrayogaSaffron.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Safety & Prototype Boundary:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PrayogaSaffron
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Project Prayoga is a research prototype and is not a medical device. It does not claim thought reading or clinical operation. Sensors must use conditioned signals with MPU6050 and piezoelectric elements.",
                            fontSize = 11.sp,
                            color = PrayogaTextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Links to GitHub and Vercel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rishipilla/Project-Prayoga-app.git"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "GitHub", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GitHub", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://prayoga-app.vercel.app/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Web", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Web App", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
