package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewMode: ViewModel() {
    val mutableLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    fun incrementCount() {
        mutableLiveData.value = mutableLiveData.value!! + 1
    }
}