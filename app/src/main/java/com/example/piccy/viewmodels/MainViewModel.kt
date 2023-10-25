package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val currentScreen: MutableLiveData<Screen> by lazy {
        MutableLiveData<Screen>(Screen.HOME)
    }
}