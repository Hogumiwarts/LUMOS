package com.hogumiwarts.data.entity.remote.Response.light

data class PatchLightColorRequest(
    val hue : Int,
    val saturation: Float
)
