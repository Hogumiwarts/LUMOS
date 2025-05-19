package com.hogumiwarts.lumos.presentation.ui.screens.gesture

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioPowerData
import com.hogumiwarts.domain.model.gesture.GestureDetailData
import com.hogumiwarts.domain.model.gesture.GestureDetailResult
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.AudioPowerState

sealed class GestureDetailState {
    object Idle : GestureDetailState()
    object Loading : GestureDetailState()
    data class Loaded(val data: GestureDetailData) : GestureDetailState()
    data class Error(val error: CommonError) : GestureDetailState()
}