package com.example.piccy.model.db

import com.example.piccy.model.auth.Authenticator
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class FirebaseDbHelper(private val authenticator: Authenticator): DbHelper{
    private val firestoreDb = Firebase.firestore
    private var userDetails: DocumentSnapshot? = null

    override fun checkUserAccountExists(onResult: (Boolean) -> Unit) {
        if (userDetails?.exists() == true) {
            onResult(true)
            return
        }

        fetchUserDetails {
            onResult(userDetails?.exists()?:false)
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

    override fun getPfpId(onComplete: (String?) -> Unit) {
        if (userDetails?.exists() == true) {
            onComplete(userDetails!!.getString("pfp"))
            return
        }

        fetchUserDetails {
            onComplete(userDetails?.getString("pfp"))
        }
    }

    private fun fetchUserDetails(onComplete: () -> Unit = {}) {
        firestoreDb
            .collection("users")
            .document(authenticator.uid)
            .get()
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    userDetails = task.result
                }
                onComplete()
            }
    }

    override fun refresh() {
        fetchUserDetails()
    }

}