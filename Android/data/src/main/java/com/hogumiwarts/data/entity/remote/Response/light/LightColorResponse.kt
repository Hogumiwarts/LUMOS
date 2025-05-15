package com.hogumiwarts.data.entity.remote.Response.light

data class LightColorResponse(
    val success: Boolean,
    val hue: Int,
    val saturation: Float
)
