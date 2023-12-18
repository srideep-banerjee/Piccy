package com.example.piccy.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.example.piccy.databinding.FragmentLoginBinding
import com.example.piccy.view.Util.Companion.addListener
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val loginFragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)

        loginFragmentBinding.signupLink.setOnClickListener {
            setFragmentResult("nextScreen", bundleOf("screenName" to ProfileScreen.SIGNUP.screenName))
        }

        val profileViewModel by activityViewModels<ProfileViewModel>()

        loginFragmentBinding.loginButton.setOnClickListener {
            profileViewModel.login()
        }

        loginFragmentBinding.emailEditTextLogin.setText(profileViewModel.currentEntries[0])
        loginFragmentBinding.passwordEditTextLogin.setText(profileViewModel.currentEntries[1])

        addListener(loginFragmentBinding.emailEditTextLogin, profileViewModel, 0)
        addListener(loginFragmentBinding.passwordEditTextLogin, profileViewModel, 1)

        return loginFragmentBinding.root
    }

}