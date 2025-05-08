package com.hogumiwarts.lumos.device.repository

import android.content.Context
import android.util.Log
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
        val input = Array(1) { Array(normalizedData.size) { FloatArray(6) } }
        for (i in normalizedData.indices) {
            for (j in 0 until 6) {
                input[0][i][j] = normalizedData[i][j]
            }
        }

        val output = Array(1) { FloatArray(labelMap.size) }

        try {
            interpreter.run(input, output)
        } catch (e: Exception) {
            Log.e("GestureRepo", "TFLite 추론 중 오류 발생", e)
            return GestureResult(label = "Error", confidence = 0f)
        }

        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][predictedIndex]
        val label = labelMap[predictedIndex] ?: "Unknown"

        return GestureResult(label = label, confidence = confidence)
    }

}