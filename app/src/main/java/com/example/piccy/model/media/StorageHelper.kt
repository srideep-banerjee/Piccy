package com.example.piccy.model.media

import android.net.Uri

interface StorageHelper {
    fun getImageById(path: String, id: String, onComplete: (Uri?)->Unit)
}