package com.example.piccy.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.piccy.databinding.FragmentDetailsBinding
import com.example.piccy.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val detailsFragmentBinding = FragmentDetailsBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        detailsFragmentBinding.userNameDisplay.text = profileViewModel.getUsername()?:""

        detailsFragmentBinding.signOutButton.setOnClickListener {
            lifecycleScope.launch {
                profileViewModel.signOut()
            }
        }

        return detailsFragmentBinding.root
    }
}