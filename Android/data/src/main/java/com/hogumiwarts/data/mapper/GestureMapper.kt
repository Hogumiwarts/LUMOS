package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.audio.GetAudioStatusResponse
import com.hogumiwarts.data.entity.remote.Response.gesture.GetGestureDetail
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.domain.model.gesture.GestureDetailData

object GestureMapper {
    fun fromGestureDetailDataResponse(response: GetGestureDetail): GestureDetailData {
        return GestureDetailData(
            gestureId = response.gestureId,
            gestureName = response.gestureName,
            gestureImageUrl = response.gestureImageUrl,
            gestureDescription = response.gestureDescription
        )

    }
}