package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.HeadsetState
import com.example.ui.components.GlassCard
import com.example.ui.components.HeadsetStatusPill
import com.example.ui.components.LiveSensorWaveformCanvas
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanDark
import com.example.ui.theme.PrayogaCyanLight
import com.example.ui.theme.PrayogaError
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassCard
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaSaffron
import com.example.ui.theme.PrayogaSuccess
import com.example.ui.theme.PrayogaTeal
import com.example.ui.theme.PrayogaTextPrimary
import com.example.ui.theme.PrayogaTextSecondary
import com.example.viewmodel.PrayogaViewModel

@Composable
fun ScannerScreen(
    viewModel: PrayogaViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val headsetState by viewModel.headsetState.collectAsState()
    val device by viewModel.device.collectAsState()
    val connectingStep by viewModel.connectingStep.collectAsState()
    val latestSample by viewModel.latestSample.collectAsState()
    val recentSamples by viewModel.recentSamples.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Laser scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    // Radar pulse animation for ESP32 connection
    val radarPulse1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_1"
    )

    val radarPulse2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_2"
    )

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
                    text = "HEADSET SCANNER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = PrayogaCyanDark
                )
                Text(
                    text = "QR Identification & ESP32 BLE Link",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrayogaTextSecondary
                )
            }
            HeadsetStatusPill(state = headsetState)
        }

        // ==========================================
        // CAMERA SCANNER VIEWFINDER / SIMULATOR
        // ==========================================
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("camera_viewfinder_card"),
            shapeRadius = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Camera QR Scanner",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrayogaTextPrimary
                )
                Text(
                    text = "Align the Prayoga headset QR code inside the viewfinder",
                    fontSize = 12.sp,
                    color = PrayogaTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    if (hasCameraPermission) {
                        // Real CameraX preview
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                    } catch (_: Exception) {}
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder viewfinder if permission pending
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR Code",
                                    tint = PrayogaCyanLight,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ready to Scan Headset QR",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrayogaCyan)
                                ) {
                                    Text("Grant Camera Permission", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Scanner Viewfinder Overlay with Laser Line
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val boxSize = canvasWidth.coerceAtMost(canvasHeight) * 0.72f
                        val left = (canvasWidth - boxSize) / 2f
                        val top = (canvasHeight - boxSize) / 2f

                        // Corner Reticles
                        drawRoundRect(
                            color = PrayogaCyanLight.copy(alpha = 0.8f),
                            topLeft = Offset(left, top),
                            size = Size(boxSize, boxSize),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Animated Laser Scanline
                        val laserY = top + (boxSize * laserYRatio)
                        drawLine(
                            color = PrayogaCyan,
                            start = Offset(left + 8.dp.toPx(), laserY),
                            end = Offset(left + boxSize - 8.dp.toPx(), laserY),
                            strokeWidth = 3.dp.toPx()
                        )
                    }

                    // Status Overlay at bottom of camera view
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = if (device.isConnected) "ESP32 IDENTIFIED: ${device.deviceId}" else "SCANNING FOR WEARABLE QR...",
                            color = if (device.isConnected) PrayogaSuccess else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // One-tap Quick Connect & Sample QR Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val qrSample = """{"deviceId":"PRAYOGA-001","deviceName":"PRAYOGA-ESP32","protocolVersion":1,"bleServiceUuid":"7a8e0001-8f3b-4cf8-9d31-9d77b3a10001"}"""
                            viewModel.connectToHeadset(qrSample)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_sample_qr_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrayogaCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Simulate Scan",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Headset QR", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    if (device.isConnected) {
                        OutlinedButton(
                            onClick = { viewModel.disconnectHeadset() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Disconnect", fontSize = 13.sp, color = PrayogaError)
                        }
                    }
                }
            }
        }

        // =======================================================
        // ANIMATION WHILE CONNECTING TO HEADSET ESP35 / ESP32
        // =======================================================
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("connecting_animation_card"),
            shapeRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ESP32 Wearable Telemetry",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrayogaTextPrimary
                )
                Text(
                    text = "MPU6050 Motion + Conditioned Piezo Sensor",
                    fontSize = 12.sp,
                    color = PrayogaTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Radar Orbit Graphic
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = size.width / 2f

                        // Radar pulse 1
                        drawCircle(
                            color = PrayogaCyan.copy(alpha = (1f - radarPulse1) * 0.4f),
                            radius = maxRadius * radarPulse1,
                            center = center
                        )

                        // Radar pulse 2
                        drawCircle(
                            color = PrayogaTeal.copy(alpha = (1f - radarPulse2) * 0.35f),
                            radius = maxRadius * radarPulse2,
                            center = center
                        )

                        // Outer rim
                        drawCircle(
                            color = PrayogaCyanLight.copy(alpha = 0.5f),
                            radius = maxRadius - 2.dp.toPx(),
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Center Headset Core
                    Surface(
                        modifier = Modifier.size(76.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (device.isConnected) PrayogaSuccess else PrayogaCyan
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (device.isConnected) Icons.Default.BluetoothConnected else Icons.Default.BluetoothSearching,
                                contentDescription = "Headset Icon",
                                tint = if (device.isConnected) PrayogaSuccess else PrayogaCyan,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connection Handshake Step
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = if (device.isConnected) PrayogaSuccess else PrayogaCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = connectingStep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrayogaTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Technical specs chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("100 Hz", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrayogaCyanDark)
                        Text("Sampling", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("300 Samples", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrayogaCyanDark)
                        Text("Ring Window", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("19 Bytes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrayogaCyanDark)
                        Text("Packet Size", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${device.batteryPercent}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrayogaSuccess)
                        Text("Battery", fontSize = 11.sp, color = PrayogaTextSecondary)
                    }
                }
            }
        }

        // ==========================================
        // 19-BYTE PACKET & WAVEFORM INSPECTOR
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
                    Text(
                        text = "Real-Time Sensor Telemetry",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrayogaTextPrimary
                    )
                    Text(
                        text = "Seq #${latestSample?.seq ?: 0}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = PrayogaTeal,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                LiveSensorWaveformCanvas(samples = recentSamples, height = 80.dp)

                Spacer(modifier = Modifier.height(12.dp))

                latestSample?.let { s ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Piezo ADC", fontSize = 11.sp, color = PrayogaTextSecondary)
                            Text("${s.piezoAdc}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrayogaCyan)
                        }
                        Column {
                            Text("Accel (mg)", fontSize = 11.sp, color = PrayogaTextSecondary)
                            Text("X:${s.ax} Y:${s.ay} Z:${s.az}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrayogaSaffron)
                        }
                        Column {
                            Text("Gyro (°/s)", fontSize = 11.sp, color = PrayogaTextSecondary)
                            Text("X:${s.gx} Y:${s.gy} Z:${s.gz}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrayogaTeal)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
