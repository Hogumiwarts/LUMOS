package com.hogumiwarts.lumos.device.repository

import android.content.Context
import com.hogumiwarts.lumos.device.ml.LabelMapLoader
import com.hogumiwarts.lumos.device.ml.TFLiteInterpreterProvider
import com.hogumiwarts.lumos.domain.model.GestureResult
import com.hogumiwarts.lumos.domain.repository.GestureRepository
import org.tensorflow.lite.Interpreter

class GestureRepositoryImpl(
    context: Context
) : GestureRepository {

    private val interpreter: Interpreter = TFLiteInterpreterProvider.getInterpreter(context)
    private val labelMap: Map<Int, String> = LabelMapLoader.loadLabelMap(context)

    override suspend fun predictGesture(normalizedData: Array<FloatArray>): GestureResult {
        val output = Array(1) { FloatArray(labelMap.size) }

        // Run model inference
        interpreter.run(normalizedData, output)

        // Find the index with the highest confidence
        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][predictedIndex]
        val label = labelMap[predictedIndex] ?: "Unknown"

        return GestureResult(label = label, confidence = confidence)
    }
}