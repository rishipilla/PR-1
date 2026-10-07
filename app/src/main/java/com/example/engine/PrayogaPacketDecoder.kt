package com.example.engine

import com.example.model.SensorSample
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Prayoga 19-byte BLE sensor packet decoder and validator as specified in the system architecture:
 *
 * Byte 0: Magic (0xA5)
 * Byte 1: Protocol version (1)
 * Bytes 2-3: Sequence number (uint16, little-endian)
 * Bytes 4-5: AX (int16, milli-g)
 * Bytes 6-7: AY (int16, milli-g)
 * Bytes 8-9: AZ (int16, milli-g)
 * Bytes 10-11: GX (int16, milli-deg/s)
 * Bytes 12-13: GY (int16, milli-deg/s)
 * Bytes 14-15: GZ (int16, milli-deg/s)
 * Bytes 16-17: Piezo ADC (uint16)
 * Byte 18: XOR checksum of bytes 0..17
 */
object PrayogaPacketDecoder {
    const val PACKET_SIZE = 19
    const val MAGIC_BYTE: Byte = 0xA5.toByte()
    const val PROTOCOL_VERSION: Byte = 1

    private var lastSequenceNumber: Int? = null
    var droppedPacketsCount: Long = 0
        private set
    var totalPacketsDecoded: Long = 0
        private set

    sealed class DecodeResult {
        data class Success(val sample: SensorSample, val sequenceGapDetected: Boolean) : DecodeResult()
        data class Error(val reason: String) : DecodeResult()
    }

    fun decodePacket(data: ByteArray): DecodeResult {
        if (data.size != PACKET_SIZE) {
            return DecodeResult.Error("Invalid packet length: ${data.size} (expected $PACKET_SIZE)")
        }

        if (data[0] != MAGIC_BYTE) {
            return DecodeResult.Error("Invalid magic byte: 0x${String.format("%02X", data[0])} (expected 0xA5)")
        }

        if (data[1] != PROTOCOL_VERSION) {
            return DecodeResult.Error("Unsupported protocol version: ${data[1]} (expected $PROTOCOL_VERSION)")
        }

        // Validate XOR Checksum over bytes 0..17
        var computedXor: Byte = 0
        for (i in 0 until 18) {
            computedXor = (computedXor.toInt() xor data[i].toInt()).toByte()
        }

        if (computedXor != data[18]) {
            return DecodeResult.Error("Checksum mismatch: computed 0x${String.format("%02X", computedXor)} != received 0x${String.format("%02X", data[18])}")
        }

        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        // Skip magic & version
        buffer.position(2)

        val seq = buffer.short.toInt() and 0xFFFF
        val ax = buffer.short.toInt()
        val ay = buffer.short.toInt()
        val az = buffer.short.toInt()
        val gx = buffer.short.toInt()
        val gy = buffer.short.toInt()
        val gz = buffer.short.toInt()
        val piezoAdc = buffer.short.toInt() and 0xFFFF

        var gapDetected = false
        lastSequenceNumber?.let { last ->
            val expectedNext = (last + 1) and 0xFFFF
            if (seq != expectedNext) {
                gapDetected = true
                droppedPacketsCount++
            }
        }
        lastSequenceNumber = seq
        totalPacketsDecoded++

        val sample = SensorSample(
            seq = seq,
            ax = ax,
            ay = ay,
            az = az,
            gx = gx,
            gy = gy,
            gz = gz,
            piezoAdc = piezoAdc,
            timestampMs = System.currentTimeMillis()
        )

        return DecodeResult.Success(sample, gapDetected)
    }

    /**
     * Builds a valid 19-byte packet for testing/simulation.
     */
    fun encodePacket(
        seq: Int,
        ax: Int,
        ay: Int,
        az: Int,
        gx: Int,
        gy: Int,
        gz: Int,
        piezoAdc: Int
    ): ByteArray {
        val bytes = ByteArray(PACKET_SIZE)
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        buffer.put(MAGIC_BYTE)
        buffer.put(PROTOCOL_VERSION)
        buffer.putShort((seq and 0xFFFF).toShort())
        buffer.putShort(ax.toShort())
        buffer.putShort(ay.toShort())
        buffer.putShort(az.toShort())
        buffer.putShort(gx.toShort())
        buffer.putShort(gy.toShort())
        buffer.putShort(gz.toShort())
        buffer.putShort((piezoAdc and 0xFFFF).toShort())

        var xorVal: Byte = 0
        for (i in 0 until 18) {
            xorVal = (xorVal.toInt() xor bytes[i].toInt()).toByte()
        }
        bytes[18] = xorVal

        return bytes
    }

    fun resetStats() {
        lastSequenceNumber = null
        droppedPacketsCount = 0
        totalPacketsDecoded = 0
    }
}
