package com.example.piccy.view

import android.app.Application
import com.google.android.material.color.DynamicColors

class PiccyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply dynamic color
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}