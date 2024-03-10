package com.example.piccy.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.piccy.R
import com.example.piccy.databinding.FragmentDetailsBinding
import com.example.piccy.viewmodels.ProfileViewModel

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val detailsFragmentBinding = FragmentDetailsBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        detailsFragmentBinding.userNameDisplay.text = profileViewModel.getUsername()?:""

        return detailsFragmentBinding.root
    }
}