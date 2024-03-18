package com.example.piccy.model.db

import com.example.piccy.model.auth.Authenticator
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FirebaseDbHelper(private val authenticator: Authenticator): DbHelper{
    val firestoreDb = Firebase.firestore
    override fun checkUserAccountExists(onResult: (Boolean) -> Unit) {
        firestoreDb
            .collection("users")
            .document(authenticator.uid)
            .get()
            .addOnCompleteListener {task ->
                onResult(task.isSuccessful && task.result?.exists()?:false)
            }
    }

    override fun createUserAccount(onComplete: (Boolean) -> Unit) {
        val user = mapOf(
            "name" to authenticator.userName
        )
        firestoreDb
            .collection("users")
            .document(authenticator.uid)
            .set(user)
            .addOnCompleteListener {
                onComplete(it.isSuccessful)
            }
    }

}