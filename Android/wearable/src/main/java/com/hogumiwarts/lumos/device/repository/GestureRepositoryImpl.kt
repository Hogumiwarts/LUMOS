package com.hogumiwarts.lumos.device.repository

import android.content.Context
import android.util.Log
import com.hogumiwarts.data.source.remote.GestureApi
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.PredictionResult
import com.hogumiwarts.lumos.device.ml.LabelMapLoader
import com.hogumiwarts.lumos.device.ml.TFLiteInterpreterProvider
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.lumos.domain.repository.GestureRepository
import org.tensorflow.lite.Interpreter
import javax.inject.Inject

class GestureRepositoryImpl @Inject constructor(
    private val gestureApi: GestureApi,
    context: Context
) : GestureRepository {

    private val interpreter: Interpreter = TFLiteInterpreterProvider.getInterpreter(context)
    private val labelMap: Map<Int, String> = LabelMapLoader.loadLabelMap(context)

    override suspend fun getGestureList(): GestureResult {
        return try {
            val response = gestureApi.getGestureList()

            val data = response.data?.map {
                com.hogumiwarts.domain.model.GestureData(
                    gestureId = it.gestureId,
                    gestureName = it.gestureName,
                    gestureDescription = it.gestureDescription,
                    gestureImageUrl = it.gestureImageUrl,
                    routineName = it.routineName ?: "",
                    routineId = it.routineId ?: 0L
                )
            }.orEmpty()

            GestureResult.Success(data)
        } catch (e: Exception) {
            Log.e("GestureRepo", "🔥 제스처 리스트 가져오기 실패: ${e.message}", e)
            GestureResult.Error(CommonError.NetworkError)
        }

    }


    override suspend fun predictGesture(normalizedData: Array<FloatArray>): PredictionResult {
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
            return PredictionResult(label = "Error", confidence = 0f)
        }

        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][predictedIndex]
        val label = labelMap[predictedIndex] ?: "Unknown"

        return PredictionResult(label = label, confidence = confidence)
    }


}