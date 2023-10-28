package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private var currentScreen = Screen.HOME

    var searchViewExpanded :MutableLiveData<Boolean> = MutableLiveData(false)
        private set

    var searchQueryText: MutableLiveData<String> = MutableLiveData("")

    fun updateScreen(screen: Screen) {
        if(screen != currentScreen) {
            searchViewExpanded.value = false
            searchQueryText.value = ""
        }
        currentScreen = screen
    }

    fun updateSearchQueryText(text: String) {
        searchQueryText.value = text
    }
}