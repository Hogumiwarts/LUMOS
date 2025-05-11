package com.hogumiwarts.lumos.device.sensor


import kotlin.math.pow
import kotlin.math.sqrt

object SensorNormalizer {

    fun normalize(data: List<FloatArray>): Array<FloatArray> {
        if (data.isEmpty()) return emptyArray()

        val dimension = data[0].size
        val means = FloatArray(dimension) { i -> data.map { it[i] }.average().toFloat() }

        val stds = FloatArray(dimension) { i ->
            val mean = means[i]
            val variance = data.map { (it[i] - mean).pow(2) }.average().toFloat()
            sqrt(variance).coerceAtLeast(1e-6f) // 0으로 나눔 방지
        }

        return data.map { row ->
            FloatArray(dimension) { i -> (row[i] - means[i]) / stds[i] }
        }.toTypedArray()
    }
}
