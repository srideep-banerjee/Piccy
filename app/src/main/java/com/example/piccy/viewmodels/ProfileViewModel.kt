package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piccy.model.auth.Authenticator
import com.example.piccy.model.auth.FirebaseAuthenticator
import com.example.piccy.model.auth.UserAuthenticationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {

    private val authenticator: Authenticator by lazy {
        FirebaseAuthenticator()
    }
    var loading = MutableLiveData(true)

    var currentScreen: MutableLiveData<ProfileScreen> = MutableLiveData(ProfileScreen.ANONYMOUS)
        private set

    var currentEntries = mutableListOf("", "", "")
        private set

    val toast: MutableLiveData<String> = MutableLiveData("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            authenticator.checkAuthState(
                {
                    val screen = when (authenticator.userAuthenticationState) {
                        UserAuthenticationState.NONE -> ProfileScreen.ANONYMOUS
                        UserAuthenticationState.REGISTERED -> ProfileScreen.VERIFICATION
                        UserAuthenticationState.VERIFIED -> ProfileScreen.DETAILS
                    }
                    viewModelScope.launch(Dispatchers.Main) {
                        currentScreen.postValue(screen)
                        loading.postValue(false)
                    }
                },
                {
                    loading.postValue(false)
                    toast.postValue(it ?: "null")
                }
            )

        }
    }

    fun updateScreen(screen: ProfileScreen) {
        if (screen == currentScreen.value) return

        currentScreen.value = screen
        currentEntries = mutableListOf("", "", "")
    }

    fun updateEntryAt(index: Int, str: String?) {
        currentEntries[index] = str ?: ""
    }

    fun login(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticator.logIn(currentEntries[0], currentEntries[1]) { successful, msg ->
                onComplete(successful, msg)
                when (authenticator.userAuthenticationState) {
                    UserAuthenticationState.NONE -> {}
                    UserAuthenticationState.REGISTERED -> {
                        updateScreen(ProfileScreen.VERIFICATION)
                    }
                    UserAuthenticationState.VERIFIED -> {
                        updateScreen(ProfileScreen.DETAILS)
                    }
                }
            }
        }
    }

    fun signup(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticator.signUp(
                currentEntries[0],
                currentEntries[1],
                currentEntries[2]
            ) { successful, msg ->
                onComplete(successful, msg)
                when (authenticator.userAuthenticationState) {
                    UserAuthenticationState.NONE -> {}
                    UserAuthenticationState.REGISTERED -> {
                        updateScreen(ProfileScreen.VERIFICATION)
                    }
                    UserAuthenticationState.VERIFIED -> {
                        updateScreen(ProfileScreen.DETAILS)
                    }
                }
            }
        }
    }

    fun resendVerificationEmail(onComplete: (Boolean) -> Unit) {
        authenticator.resendVerificationEmail {
            onComplete(it)
        }
    }

    fun getEmail(): String? {
        return authenticator.email
    }

    fun isEmailVerified(onComplete: (Boolean) -> Unit) {
        return authenticator.isVerified{
            onComplete(it)
        }
    }

    fun getUsername(): String? {
        return authenticator.userName
    }

    override fun onCleared() {
        authenticator.close()
        super.onCleared()
    }
}