package com.example.piccy.viewmodels

import androidx.lifecycle.ViewModel
import com.example.piccy.model.FirebaseAuthenticator

class ProfileViewModel : ViewModel() {

    private val firebaseAuthenticator by lazy {
        FirebaseAuthenticator()
    }

    var currentScreen = ProfileScreen.ANONYMOUS
        private set

    var currentEntries = mutableListOf("", "", "")
        private set

    fun updateScreen(screen: ProfileScreen) {
        if (screen == currentScreen) return

        currentScreen = screen
        currentEntries = mutableListOf("", "", "")
    }

    fun updateEntryAt(index: Int, str: String?) {
        val fa = firebaseAuthenticator
        currentEntries[index] = str ?: ""
    }

    fun login() {
        firebaseAuthenticator.logIn(currentEntries[0], currentEntries[1])
    }

    fun signup() {
        firebaseAuthenticator.signUp(currentEntries[0], currentEntries[1], currentEntries[2])
    }
}