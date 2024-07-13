package com.example.piccy.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piccy.model.auth.AuthKt
import com.example.piccy.model.auth.FbAuthKt
import com.example.piccy.model.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {

    private val authKt: AuthKt = FbAuthKt()

    val loading = MutableLiveData(true)

    private var currentUser: User? = null

    var currentScreen: MutableLiveData<ProfileScreen> = MutableLiveData(ProfileScreen.ANONYMOUS)
        private set

    var currentAuthScreenEntries: AuthScreenEntries? = null

    val toast: MutableLiveData<String> = MutableLiveData("")

    val pfp: MutableLiveData<Uri?> = MutableLiveData(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            authKt
                .getUserFlow()
                .collect { user ->
                    currentUser = user
                    if (loading.value != false) {
                        loading.postValue(false)
                    }
                    withContext(Dispatchers.Main) {
                        updateScreenByUser(user)
                    }
                }
        }
    }

    private fun updateScreenByUser(user: User?) {
        val screen = if (user == null) ProfileScreen.ANONYMOUS
            else if (!user.emailVerified) ProfileScreen.VERIFICATION
            else ProfileScreen.DETAILS
        updateScreen(screen)
    }

    fun updateScreen(screen: ProfileScreen) {
        if (screen == currentScreen.value) return

        currentAuthScreenEntries = when (screen) {
            ProfileScreen.LOGIN -> LogInEntries()
            ProfileScreen.SIGNUP -> SignInEntries()
            else -> null
        }
        currentScreen.value = screen
    }

    fun login(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entries = currentAuthScreenEntries as LogInEntries
                withContext(Dispatchers.IO) {
                    authKt.logIn(
                        email = entries.email,
                        password = entries.password
                    )
                }
                onComplete(true, "")
            } catch (e: ClassCastException) {
                throw e
            } catch (e: NullPointerException) {
                throw e
            } catch (e: Exception) {
                onComplete(false, e.message ?: "An error occurred while Logging In")
            }
        }
    }

    fun signup(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val entries = currentAuthScreenEntries as SignInEntries
                withContext(Dispatchers.IO) {
                    authKt.register(
                        email = entries.email,
                        password = entries.password,
                        userName = entries.userName
                    )
                }
                onComplete(true, "")
            } catch (e: ClassCastException) {
                throw e
            } catch (e: NullPointerException) {
                throw e
            } catch (e: Exception) {
                onComplete(false, e.message ?: "An error occurred while Singing In")
            }
        }
    }

    suspend fun resendVerificationEmail() {
        withContext(Dispatchers.IO) {
            authKt.resendVerificationEmail()
        }
    }

    fun getEmail() = currentUser?.email

    suspend fun isEmailVerified(): Boolean {
        return withContext(Dispatchers.IO) {
            authKt.reloadEmailVerificationState()
        }
    }

    fun getUsername() = currentUser?.userName

    fun setPfp(pfp: Uri?) {
        this.pfp.postValue(pfp)
    }

    interface AuthScreenEntries

    class SignInEntries(
        var email: String = "",
        var userName: String = "",
        var password: String = ""
    ): AuthScreenEntries

    class LogInEntries(
        var email: String = "",
        var password: String = ""
    ): AuthScreenEntries
}