package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeadsetState
import com.example.model.SensorSample
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanLight
import com.example.ui.theme.PrayogaError
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassCard
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaSaffron
import com.example.ui.theme.PrayogaSuccess
import com.example.ui.theme.PrayogaTeal
import com.example.ui.theme.PrayogaTextPrimary

/**
 * Frosted Glass Card container with crystal-white translucent gradient,
 * sleek subtle border, and soft elevated shadow.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shapeRadius: Dp = 20.dp,
    borderColor: Color = PrayogaGlassBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    backgroundColor: Color = PrayogaGlassWhite,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(shapeRadius)
    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x1A0F172A),
                spotColor = Color(0x140284C7)
            )
            .border(
                width = borderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.5f),
                        borderColor.copy(alpha = 0.15f)
                    )
                ),
                shape = shape
            ),
        shape = shape,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Animated Status Pill indicating current Headset / BLE state.
 */
@Composable
fun HeadsetStatusPill(
    state: HeadsetState,
    modifier: Modifier = Modifier
) {
    val stateColor = when (state) {
        HeadsetState.READY, HeadsetState.CONNECTED -> PrayogaSuccess
        HeadsetState.LISTENING -> PrayogaCyan
        HeadsetState.PROCESSING -> PrayogaTeal
        HeadsetState.RECOGNIZED -> PrayogaCyan
        HeadsetState.SCANNING, HeadsetState.CONNECTING -> PrayogaSaffron
        HeadsetState.UNCERTAIN -> PrayogaSaffron
        HeadsetState.DISCONNECTED, HeadsetState.ERROR -> PrayogaError
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_state")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = stateColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, stateColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(stateColor.copy(alpha = pulseAlpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = state.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = stateColor
            )
        }
    }
}

/**
 * Real-time dynamic sensor waveform visualizer.
 * Renders Piezo acoustic vibration in Teal/Cyan, and Accel dynamics in Saffron.
 */
@Composable
fun LiveSensorWaveformCanvas(
    samples: List<SensorSample>,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val canvasHeight = size.height
            val midY = canvasHeight / 2f

            // Draw grid lines
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1.dp.toPx()
            )

            if (samples.size < 2) {
                // Static baseline
                return@Canvas
            }

            val stepX = width / (samples.size - 1).coerceAtLeast(1)

            // Draw Accel Path (Saffron)
            val accelPath = Path()
            samples.forEachIndexed { i, s ->
                val x = i * stepX
                val normalizedY = midY - ((s.ax / 1000f) * (canvasHeight * 0.35f))
                if (i == 0) accelPath.moveTo(x, normalizedY) else accelPath.lineTo(x, normalizedY)
            }
            drawPath(
                path = accelPath,
                color = PrayogaSaffron.copy(alpha = 0.8f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Piezo Vibration Path (Cyan/Teal)
            val piezoPath = Path()
            val piezoMean = samples.map { it.piezoAdc }.average().toFloat().takeIf { it > 0 } ?: 512f
            samples.forEachIndexed { i, s ->
                val x = i * stepX
                val deviation = (s.piezoAdc - piezoMean) / 600f
                val clampedDev = deviation.coerceIn(-1f, 1f)
                val y = midY - (clampedDev * (canvasHeight * 0.42f))
                if (i == 0) piezoPath.moveTo(x, y) else piezoPath.lineTo(x, y)
            }
            drawPath(
                path = piezoPath,
                color = PrayogaCyan,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
