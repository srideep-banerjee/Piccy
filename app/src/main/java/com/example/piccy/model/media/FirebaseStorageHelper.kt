package com.example.piccy.model.media

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class FirebaseStorageHelper: StorageHelper {
    val storageReference = Firebase.storage.reference

    override fun getImageById(path: String, id: String, onComplete: (Uri?)->Unit) {
        storageReference
            .child("$path/$id.webp")
            .downloadUrl
            .addOnCompleteListener {
                onComplete(if (it.isSuccessful) it.result else null)
            }
    }

}