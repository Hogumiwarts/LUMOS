package com.example.myapplication.data.model

data class ImuData(
    val ground_truth: Long,
    val predicted: Long,
    val match: Boolean,
)
