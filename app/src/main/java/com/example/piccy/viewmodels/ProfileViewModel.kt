package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    var currentScreen = MutableLiveData<ProfileScreen>(ProfileScreen.ANONYMOUS)
        private set

    var currentEntries = listOf("", "", "")
        private set

    fun updateScreen(screen: ProfileScreen) {
        println("Update screen called, new screen: "+screen.screenName)
        if (screen == currentScreen.value) return

        currentScreen.value = screen
        currentEntries = listOf("", "", "")
    }

    fun updateEntries(newEntries: List<String>) {
        currentEntries = newEntries
    }
}