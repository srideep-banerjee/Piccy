package com.example.piccy.model.auth

import android.util.Log
import com.example.piccy.BuildConfig
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class FbAuthKt: AuthKt {
    private val emailVerificationStateFlow = MutableStateFlow(false)
    private val userNameStateFlow = MutableStateFlow<String?>(null)

    init {
        if (BuildConfig.USE_EMULATOR) {
            FirebaseAuth
                .getInstance()
                .useEmulator(BuildConfig.EMULATOR_IP, 9099)
        }
    }
    override fun getUserFlow(): Flow<User?> {
        val userFlow =  callbackFlow {
            val firebaseAuth = FirebaseAuth.getInstance()
            val authStateListener = FirebaseAuth.AuthStateListener {auth ->
                val user = auth.currentUser?.let {
                    User(
                        uid = it.uid,
                        email = it.email!!,
                        userName = it.displayName,
                        emailVerified = it.isEmailVerified
                    )
                }
                this.trySend(user)
                    .onFailure {
                        Log.e("PICCY_LOGS", "Error in UserFlow")
                        Log.e("PICCY_LOGS", "", it)
                    }
            }
            firebaseAuth.addAuthStateListener(authStateListener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
        return userFlow
            .combine(emailVerificationStateFlow) {user, verified ->
                user?.let {
                    it.copy(emailVerified = it.emailVerified || verified)
                }
            }
            .combine(userNameStateFlow) {user, userName ->
                user?.let {
                    if (user.userName == null && userName != null) {
                        it.copy(
                            userName = userName
                        )
                    } else it

                }
            }
    }

    override suspend fun register(email: String, password: String, userName: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val authResult = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
        val updateProfileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(userName)
            .build()
        authResult
            .user!!
            .updateProfile(updateProfileChangeRequest)
            .await()
        userNameStateFlow.value = userName
        authResult
            .user!!
            .sendEmailVerification()
            .await()
    }

    override suspend fun logIn(email: String, password: String) {
        val credentials = EmailAuthProvider.getCredential(email, password)
        FirebaseAuth
            .getInstance()
            .signInWithCredential(credentials)
            .await()
    }

    override suspend fun reloadEmailVerificationState(): Boolean {
        FirebaseAuth.getInstance().currentUser?.let {
            it.reload().await()
            if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                emailVerificationStateFlow.value = true
                return true
            }
        }
        return false
    }

    override suspend fun resendVerificationEmail() {
        FirebaseAuth
            .getInstance()
            .currentUser!!
            .sendEmailVerification()
            .await()
    }

    override suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}