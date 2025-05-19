package com.hogumiwarts.lumos.ui.screens.routine.routineCreate

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableDeviceCardWithHint(
    deviceId: Long,
    shouldShowHint: Boolean,
    onHintShown: () -> Unit,
    onDelete: () -> Unit,
    deviceContent: @Composable () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    var offsetX = remember(deviceId) { mutableStateOf(0) }
    val animatedOffsetX by animateIntAsState(
        targetValue = offsetX.value,
        animationSpec = tween(durationMillis = 400),
        label = "swipe hint animation"
    )

    LaunchedEffect(shouldShowHint) {
        if (shouldShowHint) {
            offsetX.value = -80
            delay(400)
            offsetX.value = 0
            onHintShown()
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF26D6D), shape = RoundedCornerShape(10.dp))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            Box(
                modifier = Modifier.offset { IntOffset(animatedOffsetX, 0) }
            ) {
                deviceContent()
            }
        }
    )
}