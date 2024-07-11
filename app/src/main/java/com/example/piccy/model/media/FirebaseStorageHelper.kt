package com.example.piccy.model.media

import android.net.Uri
import com.example.piccy.model.auth.Authenticator
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class FirebaseStorageHelper(private val authenticator: Authenticator): StorageHelper {
    val storageReference = Firebase.storage.reference

    override fun getImageById(path: String, id: String, onComplete: (Uri?)->Unit) {
        storageReference
            .child("$path/$id.webp")
            .downloadUrl
            .addOnCompleteListener {
                onComplete(if (it.isSuccessful) it.result else null)
            }
    }

    override fun getPfpUrl(onComplete: (Uri?) -> Unit){
        storageReference
            .child("users/${authenticator.uid}/pfp")
            .downloadUrl
            .addOnCompleteListener {
                onComplete(if (it.isSuccessful) it.result else null)
            }
    }

    override fun setPfp() {
        TODO("Not yet implemented")
    }

}