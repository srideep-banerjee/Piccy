package com.example.piccy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piccy.model.auth.Authenticator
import com.example.piccy.model.auth.FirebaseAuthenticator
import com.example.piccy.model.auth.UserAuthenticationState
import com.example.piccy.model.db.DbHelper
import com.example.piccy.model.db.FirebaseDbHelper
import com.example.piccy.model.media.FirebaseStorageHelper
import com.example.piccy.model.media.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val authenticator: Authenticator by lazy {
        FirebaseAuthenticator()
    }
    private val dbHelper: DbHelper by lazy {
        FirebaseDbHelper(authenticator)
    }
    private val storageHelper: StorageHelper by lazy {
        FirebaseStorageHelper(authenticator)
    }
    private var userAuthenticationState = UserAuthenticationState.NONE

    private var currentScreen = MainScreen.HOME
    var searchViewExpanded :MutableLiveData<Boolean> = MutableLiveData(false)
        private set
    var searchQueryText: MutableLiveData<String> = MutableLiveData("")
        private set
    var toast: MutableLiveData<String> = MutableLiveData("")
        private set

    //null -> user account non existent, "" -> user account exists without pfp, "pfpid"
    var pfp: MutableLiveData<String?> = MutableLiveData(null)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            authenticator.checkAuthState(
                {
                    userAuthenticationState = it
                    performAccountOperations()
                },
                {
                    toast.postValue(it ?: "")
                }
            )
        }
    }

    private fun performAccountOperations() {
        if (userAuthenticationState == UserAuthenticationState.VERIFIED) {
            dbHelper.checkUserAccountExists { accountExists ->
                if (accountExists) {
                    dbHelper.getPfpId {
                        pfp.postValue(it ?: "")
                    }
                } else {
                    dbHelper.createUserAccount {}
                }
            }
        }
    }

    fun updateScreen(screen: MainScreen) {
        if(screen != currentScreen) {
            searchViewExpanded.value = false
            searchQueryText.value = ""
        }
        currentScreen = screen
    }

    fun updateSearchQueryText(text: String) {
        searchQueryText.value = text
    }

    fun updateIsEmailVerified() {
        authenticator.isVerified {
            if (it) {
                userAuthenticationState = UserAuthenticationState.VERIFIED
                performAccountOperations()
            }
        }
    }

    override fun onCleared() {
        authenticator.close()
        super.onCleared()
    }
}