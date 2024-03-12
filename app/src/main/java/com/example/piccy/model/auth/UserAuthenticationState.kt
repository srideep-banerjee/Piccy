package com.example.piccy.model.auth

import androidx.annotation.NonNull

enum class UserAuthenticationState {
    @NonNull NONE, @NonNull REGISTERED, @NonNull VERIFIED
}