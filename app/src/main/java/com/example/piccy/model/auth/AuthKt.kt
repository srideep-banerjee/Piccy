package com.example.piccy.model.auth

import kotlinx.coroutines.flow.Flow

interface AuthKt {
    fun getUserFlow(): Flow<User?>
    suspend fun register(email: String, password: String, userName: String)
    suspend fun logIn(email: String, password: String)
    suspend fun reloadEmailVerificationState(): Boolean
    suspend fun resendVerificationEmail()
    suspend fun signOut()
}