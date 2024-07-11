package com.example.piccy.model.media

import android.net.Uri

interface StorageHelper {
    fun getImageById(path: String, id: String, onComplete: (Uri?)->Unit)
    fun getPfpUrl(onComplete: (Uri?) -> Unit)
    fun setPfp()
}