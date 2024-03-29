package com.example.piccy.model.db

interface DbHelper {
    fun checkUserAccountExists(onResult: (Boolean)->Unit)
    fun createUserAccount(onComplete: (Boolean)->Unit)
    fun getPfpId(onComplete: (String?) -> Unit)
    fun refresh()

}