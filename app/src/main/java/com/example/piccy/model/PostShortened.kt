package com.example.piccy.model

import androidx.compose.runtime.Immutable

@Immutable
data class PostShortened(
    val title: String,
    val network: String,
    val url: String,
    val likes: Int,
    val comments: Int,
)