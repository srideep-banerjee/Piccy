package com.example.piccy.viewmodels

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    var currentScreen = ProfileScreen.ANONYMOUS
        private set

    var currentEntries = mutableListOf("", "", "")
        private set

    fun updateScreen(screen: ProfileScreen) {
        if (screen == currentScreen) return

        currentScreen = screen
        currentEntries = mutableListOf("", "", "")
    }

    fun updateEntryAt(index: Int, editable: Editable?) {
        currentEntries[index] = editable?.toString() ?: ""
    }

    fun updateEntryAt(index: Int, str: String?) {
        currentEntries[index] = str ?: ""
    }
}