package com.example

import com.example.engine.PrayogaLocalML
import com.example.engine.PrayogaPacketDecoder
import com.example.model.SensorSample
import com.example.model.VoicePhrase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayogaPacketDecoderTest {

    @Test
    fun testEncodeAndDecode19BytePacket() {
        val encodedBytes = PrayogaPacketDecoder.encodePacket(
            seq = 42,
            ax = 150,
            ay = -200,
            az = 980,
            gx = 30,
            gy = -45,
            gz = 12,
            piezoAdc = 1840
        )

        assertEquals(19, encodedBytes.size)
        assertEquals(0xA5.toByte(), encodedBytes[0])
        assertEquals(1.toByte(), encodedBytes[1])

        val result = PrayogaPacketDecoder.decodePacket(encodedBytes)
        assertTrue(result is PrayogaPacketDecoder.DecodeResult.Success)

        val sample = (result as PrayogaPacketDecoder.DecodeResult.Success).sample
        assertEquals(42, sample.seq)
        assertEquals(150, sample.ax)
        assertEquals(-200, sample.ay)
        assertEquals(980, sample.az)
        assertEquals(30, sample.gx)
        assertEquals(-45, sample.gy)
        assertEquals(12, sample.gz)
        assertEquals(1840, sample.piezoAdc)
    }

    @Test
    fun testFeatureExtractionAndClassification() {
        val samples = List(300) { idx ->
            SensorSample(
                seq = idx,
                ax = 100 + (idx % 10),
                ay = 50,
                az = 980,
                gx = 20,
                gy = 15,
                gz = 10,
                piezoAdc = 1200 + (if (idx in 100..180) 600 else 0)
            )
        }

        val features = PrayogaLocalML.extractFeatures(samples)
        assertTrue(features.piezoEnergy > 0f)
        assertTrue(features.accelMagnitudeMean > 0f)

        val vocabulary = listOf(
            VoicePhrase(id = "1", phrase = "HELLO", spokenText = "Hello."),
            VoicePhrase(id = "2", phrase = "I NEED WATER", spokenText = "I need water.")
        )

        val result = PrayogaLocalML.classifyPhrase(
            samples = samples,
            vocabulary = vocabulary,
            confidenceThreshold = 0.85f
        )

        assertNotNull(result)
        assertTrue(result.confidence in 0.70f..0.99f)
        assertEquals(300, result.sampleCount)
    }
}
