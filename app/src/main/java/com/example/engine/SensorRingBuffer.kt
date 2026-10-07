package com.example.engine

import com.example.model.SensorSample
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 300-sample circular buffer for Project Prayoga (100 Hz sampling rate = ~3.0s recognition window).
 */
class SensorRingBuffer(val capacity: Int = 300) {
    private val buffer = ConcurrentLinkedQueue<SensorSample>()

    fun addSample(sample: SensorSample) {
        buffer.add(sample)
        while (buffer.size > capacity) {
            buffer.poll()
        }
    }

    fun getSamples(): List<SensorSample> {
        return buffer.toList()
    }

    val currentCount: Int
        get() = buffer.size

    val isFull: Boolean
        get() = buffer.size >= capacity

    val fillPercentage: Float
        get() = (buffer.size.toFloat() / capacity.toFloat()).coerceIn(0f, 1f)

    fun clear() {
        buffer.clear()
    }
}
