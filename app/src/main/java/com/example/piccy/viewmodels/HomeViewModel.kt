package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewMode: ViewModel() {
    val mutableLiveData = MutableLiveData(0)

    fun incrementCount() {
        mutableLiveData.value = mutableLiveData.value!! + 1
    }
}