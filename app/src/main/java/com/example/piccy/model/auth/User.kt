package com.example.piccy.model.auth

data class User (
    val uid: String,
    val userName: String?,
    val email: String,
    val emailVerified: Boolean
)