package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.PrayogaLocalML
import com.example.engine.PrayogaPacketDecoder
import com.example.engine.PrayogaSpeechManager
import com.example.engine.SensorRingBuffer
import com.example.model.HeadsetDevice
import com.example.model.HeadsetState
import com.example.model.ModelEvidence
import com.example.model.RecognitionEvent
import com.example.model.SensorSample
import com.example.model.UserSettings
import com.example.model.VoicePhrase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.sin

class PrayogaViewModel(application: Application) : AndroidViewModel(application) {

    private val speechManager = PrayogaSpeechManager(application)
    private val ringBuffer = SensorRingBuffer(capacity = 300)

    // Core States
    private val _headsetState = MutableStateFlow(HeadsetState.READY)
    val headsetState: StateFlow<HeadsetState> = _headsetState.asStateFlow()

    private val _device = MutableStateFlow(
        HeadsetDevice(
            deviceId = "PRAYOGA-001",
            deviceName = "PRAYOGA-ESP32",
            isConnected = true,
            batteryPercent = 92,
            rssi = -54
        )
    )
    val device: StateFlow<HeadsetDevice> = _device.asStateFlow()

    // 5 Primary Supported Vocabulary + Custom Voice Datasets
    private val _vocabulary = MutableStateFlow(
        listOf(
            VoicePhrase(id = "1", phrase = "HELLO", spokenText = "Hello.", category = "Social", confidence = 0.96f),
            VoicePhrase(id = "2", phrase = "YES", spokenText = "Yes.", category = "Response", confidence = 0.94f),
            VoicePhrase(id = "3", phrase = "NO", spokenText = "No.", category = "Response", confidence = 0.93f),
            VoicePhrase(id = "4", phrase = "I NEED WATER", spokenText = "I need water.", category = "Needs", confidence = 0.95f),
            VoicePhrase(id = "5", phrase = "I NEED HELP", spokenText = "I need help.", category = "Emergency", confidence = 0.97f)
        )
    )
    val vocabulary: StateFlow<List<VoicePhrase>> = _vocabulary.asStateFlow()

    // Real-Time Sensor Stream Flow
    private val _latestSample = MutableStateFlow<SensorSample?>(null)
    val latestSample: StateFlow<SensorSample?> = _latestSample.asStateFlow()

    private val _recentSamples = MutableStateFlow<List<SensorSample>>(emptyList())
    val recentSamples: StateFlow<List<SensorSample>> = _recentSamples.asStateFlow()

    private val _bufferFill = MutableStateFlow(0f)
    val bufferFill: StateFlow<Float> = _bufferFill.asStateFlow()

    // Recognition & Pipeline
    private val _recentRecognitions = MutableStateFlow<List<RecognitionEvent>>(
        listOf(
            RecognitionEvent(
                phrase = "HELLO",
                spokenText = "Hello.",
                confidence = 0.94f,
                sampleCount = 300,
                latencyMs = 22,
                accepted = true,
                timestamp = System.currentTimeMillis() - 120000
            )
        )
    )
    val recentRecognitions: StateFlow<List<RecognitionEvent>> = _recentRecognitions.asStateFlow()

    private val _latestRecognition = MutableStateFlow<RecognitionEvent?>(
        _recentRecognitions.value.firstOrNull()
    )
    val latestRecognition: StateFlow<RecognitionEvent?> = _latestRecognition.asStateFlow()

    // Model Provenance
    private val _modelEvidence = MutableStateFlow(ModelEvidence())
    val modelEvidence: StateFlow<ModelEvidence> = _modelEvidence.asStateFlow()

    // Settings
    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    // Connecting progress animation state
    private val _connectingStep = MutableStateFlow("Initializing...")
    val connectingStep: StateFlow<String> = _connectingStep.asStateFlow()

    // Active speech indicator
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // Background streaming simulation job
    private var streamJob: Job? = null
    private var recognitionJob: Job? = null
    private var sequenceCounter = 1

    init {
        speechManager.onSpeechStarted = {
            _isSpeaking.value = true
        }
        speechManager.onSpeechDone = {
            _isSpeaking.value = false
        }
        startBackgroundSensorStream()
    }

    private fun startBackgroundSensorStream() {
        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            while (true) {
                delay(20) // ~50-100Hz tick
                if (_headsetState.value != HeadsetState.DISCONNECTED &&
                    _headsetState.value != HeadsetState.CONNECTING &&
                    _headsetState.value != HeadsetState.SCANNING
                ) {
                    val seq = sequenceCounter++
                    val t = System.currentTimeMillis() / 100.0
                    // Realistic quiescent bio-motion & piezo baseline
                    val isListening = _headsetState.value == HeadsetState.LISTENING
                    val piezoNoise = if (isListening) (sin(t * 3.5) * 650).toInt() + 1200 else (sin(t) * 80).toInt() + 512
                    val ax = ((sin(t * 0.8) * (if (isListening) 420 else 45))).toInt()
                    val ay = ((sin(t * 1.1) * (if (isListening) 350 else 30))).toInt()
                    val az = 1000 + ((sin(t * 0.5) * 40)).toInt()
                    val gx = (sin(t * 2.0) * (if (isListening) 280 else 20)).toInt()
                    val gy = (sin(t * 1.7) * (if (isListening) 220 else 15)).toInt()
                    val gz = (sin(t * 1.3) * (if (isListening) 190 else 10)).toInt()

                    val packetBytes = PrayogaPacketDecoder.encodePacket(
                        seq = seq,
                        ax = ax,
                        ay = ay,
                        az = az,
                        gx = gx,
                        gy = gy,
                        gz = gz,
                        piezoAdc = piezoNoise
                    )

                    when (val decodeResult = PrayogaPacketDecoder.decodePacket(packetBytes)) {
                        is PrayogaPacketDecoder.DecodeResult.Success -> {
                            val sample = decodeResult.sample
                            _latestSample.value = sample
                            ringBuffer.addSample(sample)
                            _bufferFill.value = ringBuffer.fillPercentage
                            _recentSamples.value = ringBuffer.getSamples().takeLast(50)
                        }
                        is PrayogaPacketDecoder.DecodeResult.Error -> {
                            // Ignored in simulation
                        }
                    }
                }
            }
        }
    }

    /**
     * Connect to Headset ESP32 with progressive animation steps.
     */
    fun connectToHeadset(qrJsonString: String? = null) {
        viewModelScope.launch {
            _headsetState.value = HeadsetState.SCANNING
            _connectingStep.value = "Scanning for ESP32 Headset beacon..."
            delay(900)

            // Parse QR if provided
            if (!qrJsonString.isNullOrBlank()) {
                try {
                    val json = JSONObject(qrJsonString)
                    val devId = json.optString("deviceId", "PRAYOGA-001")
                    val devName = json.optString("deviceName", "PRAYOGA-ESP32")
                    val uuid = json.optString("bleServiceUuid", "7a8e0001-8f3b-4cf8-9d31-9d77b3a10001")
                    _device.value = _device.value.copy(
                        deviceId = devId,
                        deviceName = devName,
                        bleServiceUuid = uuid
                    )
                } catch (_: Exception) {}
            }

            _headsetState.value = HeadsetState.CONNECTING
            _connectingStep.value = "Authenticating BLE service (${_device.value.deviceId})..."
            delay(1000)

            _connectingStep.value = "Verifying 19-byte sensor packet decoder & MPU6050..."
            delay(800)

            _connectingStep.value = "Synchronizing 300-sample buffer at 100 Hz..."
            delay(700)

            _device.value = _device.value.copy(isConnected = true)
            _headsetState.value = HeadsetState.CONNECTED
            _connectingStep.value = "Connection established!"
            delay(500)

            _headsetState.value = HeadsetState.READY
        }
    }

    fun disconnectHeadset() {
        _device.value = _device.value.copy(isConnected = false)
        _headsetState.value = HeadsetState.DISCONNECTED
    }

    /**
     * Initiates the 300-sample recognition window for silent articulation.
     */
    fun triggerArticulationRecognition(targetPhrase: VoicePhrase? = null) {
        if (recognitionJob?.isActive == true) return

        recognitionJob = viewModelScope.launch {
            _headsetState.value = HeadsetState.LISTENING
            ringBuffer.clear()

            // Collect 300 samples (~2.0 - 2.5 seconds window)
            val collectionSteps = 25
            for (i in 1..collectionSteps) {
                delay(90)
                _bufferFill.value = (i.toFloat() / collectionSteps).coerceIn(0f, 1f)
            }

            _headsetState.value = HeadsetState.PROCESSING
            delay(400) // Local ML inference computation

            val samples = ringBuffer.getSamples().ifEmpty {
                // Fallback generated samples if buffer empty
                List(300) { idx ->
                    SensorSample(
                        seq = idx,
                        ax = 120,
                        ay = 80,
                        az = 980,
                        gx = 50,
                        gy = 30,
                        gz = 20,
                        piezoAdc = 1500
                    )
                }
            }

            val result = PrayogaLocalML.classifyPhrase(
                samples = samples,
                vocabulary = _vocabulary.value,
                confidenceThreshold = _userSettings.value.confidenceThreshold,
                forcedTargetPhrase = targetPhrase
            )

            _latestRecognition.value = result
            _recentRecognitions.value = listOf(result) + _recentRecognitions.value.take(19)

            if (result.accepted) {
                _headsetState.value = HeadsetState.RECOGNIZED
                if (_userSettings.value.phoneSpeakerEnabled) {
                    speechManager.speak(
                        text = result.spokenText,
                        pitch = _userSettings.value.voicePitch,
                        rate = _userSettings.value.speechRate
                    )
                }
            } else {
                _headsetState.value = HeadsetState.UNCERTAIN
            }

            delay(2500)
            if (_headsetState.value == HeadsetState.RECOGNIZED || _headsetState.value == HeadsetState.UNCERTAIN) {
                _headsetState.value = HeadsetState.READY
            }
        }
    }

    /**
     * Speaks any text using TTS directly for test or manual click.
     */
    fun speakText(text: String) {
        speechManager.speak(
            text = text,
            pitch = _userSettings.value.voicePitch,
            rate = _userSettings.value.speechRate
        )
    }

    fun stopSpeaking() {
        speechManager.stop()
        _isSpeaking.value = false
    }

    // Voice Datasets Management
    fun addVoicePhrase(phrase: String, spokenText: String, category: String) {
        val trimmedPhrase = phrase.trim().uppercase()
        val trimmedSpoken = spokenText.trim().ifEmpty { trimmedPhrase }
        val newPhrase = VoicePhrase(
            phrase = trimmedPhrase,
            spokenText = trimmedSpoken,
            category = category.trim().ifEmpty { "Custom" },
            isCustom = true,
            confidence = 0.92f
        )
        _vocabulary.value = _vocabulary.value + newPhrase
    }

    fun deleteVoicePhrase(phraseId: String) {
        _vocabulary.value = _vocabulary.value.filterNot { it.id == phraseId }
    }

    fun exportDatasetsToJson(): String {
        val jsonArray = JSONArray()
        _vocabulary.value.forEach { item ->
            val obj = JSONObject()
            obj.put("phrase", item.phrase)
            obj.put("spokenText", item.spokenText)
            obj.put("category", item.category)
            obj.put("isCustom", item.isCustom)
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importDatasetsFromJson(jsonString: String): Boolean {
        return try {
            val jsonArray = JSONArray(jsonString)
            val importedList = mutableListOf<VoicePhrase>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val phrase = obj.getString("phrase")
                val spokenText = obj.getString("spokenText")
                val category = obj.optString("category", "Imported")
                importedList.add(
                    VoicePhrase(
                        phrase = phrase.uppercase(),
                        spokenText = spokenText,
                        category = category,
                        isCustom = true
                    )
                )
            }
            if (importedList.isNotEmpty()) {
                // Merge unique by phrase
                val existingPhrases = _vocabulary.value.map { it.phrase }.toSet()
                val newItems = importedList.filterNot { it.phrase in existingPhrases }
                _vocabulary.value = _vocabulary.value + newItems
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    // Settings Updates
    fun updateConfidenceThreshold(threshold: Float) {
        _userSettings.value = _userSettings.value.copy(confidenceThreshold = threshold)
    }

    fun updateVoicePitch(pitch: Float) {
        _userSettings.value = _userSettings.value.copy(voicePitch = pitch)
    }

    fun updateSpeechRate(rate: Float) {
        _userSettings.value = _userSettings.value.copy(speechRate = rate)
    }

    fun updateSpeakerRouting(phoneSpeaker: Boolean, headsetSpeaker: Boolean) {
        _userSettings.value = _userSettings.value.copy(
            phoneSpeakerEnabled = phoneSpeaker,
            headsetSpeakerEnabled = headsetSpeaker
        )
    }

    fun updateHapticFeedback(enabled: Boolean) {
        _userSettings.value = _userSettings.value.copy(hapticFeedback = enabled)
    }

    fun updateUserName(name: String) {
        _userSettings.value = _userSettings.value.copy(userName = name)
    }

    override fun onCleared() {
        super.onCleared()
        streamJob?.cancel()
        recognitionJob?.cancel()
        speechManager.shutdown()
    }
}
