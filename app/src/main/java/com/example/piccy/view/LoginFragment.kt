package com.example.piccy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.piccy.R
import com.example.piccy.databinding.FragmentLoginBinding
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val loginFragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        loginFragmentBinding.signupLink.setOnClickListener {
            profileViewModel.currentScreen.value = ProfileScreen.SIGNUP
        }

        return loginFragmentBinding.root
    }

}