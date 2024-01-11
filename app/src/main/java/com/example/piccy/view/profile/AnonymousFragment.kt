package com.example.piccy.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.piccy.databinding.FragmentAnonymousBinding
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class AnonymousFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anonymousBinding = FragmentAnonymousBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        anonymousBinding.anonymousLoginBtn.setOnClickListener {
            profileViewModel.updateScreen(ProfileScreen.LOGIN)
        }

        anonymousBinding.anonymousSignupBtn.setOnClickListener {
            profileViewModel.updateScreen(ProfileScreen.SIGNUP)
        }

        return anonymousBinding.root
    }
}