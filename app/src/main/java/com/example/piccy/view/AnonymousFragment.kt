package com.example.piccy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.example.piccy.databinding.FragmentAnonymousBinding
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class AnonymousFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val anonymousBinding = FragmentAnonymousBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        anonymousBinding.anonymousLoginBtn.setOnClickListener {
            //profileViewModel.updateScreen(ProfileScreen.LOGIN)
            setFragmentResult("nextScreen", bundleOf("screenName" to ProfileScreen.LOGIN.screenName))
        }

        anonymousBinding.anonymousSignupBtn.setOnClickListener {
            //profileViewModel.updateScreen(ProfileScreen.SIGNUP)
            setFragmentResult("nextScreen", bundleOf("screenName" to ProfileScreen.SIGNUP.screenName))
        }

        return anonymousBinding.root
    }
}