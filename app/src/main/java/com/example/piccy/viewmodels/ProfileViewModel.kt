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

    var currentScreen: MutableLiveData<ProfileScreen> = MutableLiveData(ProfileScreen.ANONYMOUS)
        private set

    var currentEntries = mutableListOf("", "", "")
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val screen = when ((authenticator as FirebaseAuthenticator).userAuthenticationState) {
                UserAuthenticationState.NONE -> ProfileScreen.ANONYMOUS
                UserAuthenticationState.REGISTERED -> ProfileScreen.VERIFICATION
                UserAuthenticationState.VERIFIED -> ProfileScreen.DETAILS
            }
            withContext(Dispatchers.Main){
                currentScreen.postValue(screen)
            }
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
                when ((authenticator as FirebaseAuthenticator).userAuthenticationState) {
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
                when ((authenticator as FirebaseAuthenticator).userAuthenticationState) {
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
        if (authenticator is FirebaseAuthenticator) {
            (authenticator as FirebaseAuthenticator).resendVerificationEmail {
                onComplete(it)
            }
        } else onComplete(false)
    }

    fun getEmail(): String? {
        return (authenticator as FirebaseAuthenticator).email
    }

    fun isEmailVerified(onComplete: (Boolean) -> Unit) {
        return (authenticator as FirebaseAuthenticator).isVerified{
            onComplete(it)
        }
    }

    override fun onCleared() {
        authenticator.close()
        super.onCleared()
    }
}