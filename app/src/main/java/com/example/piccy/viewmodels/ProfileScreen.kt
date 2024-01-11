package com.example.piccy.viewmodels

enum class ProfileScreen(val screenName: String) {
    ANONYMOUS("AnonymousFragment"),
    SIGNUP("SignupFragment"),
    LOGIN("LoginFragment"),
    VERIFICATION("VerificationFragment"),
    DETAILS("DetailsFragment");

    companion object {
        private val nameToOrdinalMap = HashMap<String, ProfileScreen>()

        init {
            for (value in ProfileScreen.values())
                nameToOrdinalMap[value.screenName] = value
        }

        fun getTypeByName(name: String) = nameToOrdinalMap[name]!!
    }
}