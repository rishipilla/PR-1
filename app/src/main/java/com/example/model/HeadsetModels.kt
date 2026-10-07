package com.example.model

import java.util.UUID
import kotlin.math.sqrt

enum class HeadsetState(val label: String, val description: String) {
    DISCONNECTED("Disconnected", "Headset not paired or out of range"),
    SCANNING("Scanning", "Searching for ESP32 headset via BLE..."),
    CONNECTING("Connecting", "Establishing secure BLE handshake with ESP32..."),
    CONNECTED("Connected", "BLE link active, initializing sensor stream"),
    READY("Ready to Articulate", "300-sample ring buffer active at 100 Hz"),
    LISTENING("Listening...", "Capturing silent articulation motion & piezo vibration"),
    PROCESSING("Processing", "Computing feature extraction & ML inference"),
    RECOGNIZED("Recognized", "Phrase validated and spoken through phone"),
    UNCERTAIN("Uncertain (Try Again)", "Confidence below threshold, not spoken"),
    ERROR("Connection Error", "Check headset power and try re-scanning")
}

data class HeadsetDevice(
    val deviceId: String = "PRAYOGA-001",
    val deviceName: String = "PRAYOGA-ESP32",
    val protocolVersion: Int = 1,
    val bleServiceUuid: String = "7a8e0001-8f3b-4cf8-9d31-9d77b3a10001",
    val isConnected: Boolean = false,
    val batteryPercent: Int = 94,
    val rssi: Int = -52,
    val firmwareVersion: String = "v1.4.2-esp32"
)

data class SensorSample(
    val seq: Int,
    val ax: Int, // milli-g
    val ay: Int, // milli-g
    val az: Int, // milli-g
    val gx: Int, // milli-deg/s
    val gy: Int, // milli-deg/s
    val gz: Int, // milli-deg/s
    val piezoAdc: Int, // uint16 raw ADC piezo reading
    val timestampMs: Long = System.currentTimeMillis()
) {
    val accelMagnitude: Float
        get() = sqrt((ax.toFloat() * ax + ay.toFloat() * ay + az.toFloat() * az))

    val gyroMagnitude: Float
        get() = sqrt((gx.toFloat() * gx + gy.toFloat() * gy + gz.toFloat() * gz))
}

data class VoicePhrase(
    val id: String = UUID.randomUUID().toString(),
    val phrase: String,
    val spokenText: String,
    val category: String = "General",
    val confidence: Float = 0.94f,
    val isCustom: Boolean = false,
    val iconName: String = "chat",
    val usageCount: Int = 0
)

data class RecognitionEvent(
    val id: String = UUID.randomUUID().toString(),
    val phrase: String,
    val spokenText: String,
    val confidence: Float,
    val sampleCount: Int = 300,
    val latencyMs: Long = 24,
    val accepted: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ModelEvidence(
    val modelName: String = "Prayoga SilentArticulation RF/SVM",
    val version: String = "v1.2.0-rf-svm",
    val sha256: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    val featureCount: Int = 32,
    val macroF1: Float = 0.942f,
    val accuracy: Float = 0.958f,
    val worstClassRecall: Float = 0.914f,
    val latencyMs: Int = 24,
    val windowSizeSamples: Int = 300,
    val samplingRateHz: Int = 100,
    val verified: Boolean = true
)

data class UserSettings(
    val userName: String = "Dr. Rishi Pilla",
    val userRole: String = "Lead Investigator",
    val voicePitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val phoneSpeakerEnabled: Boolean = true,
    val headsetSpeakerEnabled: Boolean = false,
    val hapticFeedback: Boolean = true,
    val confidenceThreshold: Float = 0.85f,
    val autoReconnect: Boolean = true,
    val demoMode: Boolean = true
)
