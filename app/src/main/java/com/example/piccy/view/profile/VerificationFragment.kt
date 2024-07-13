package com.example.piccy.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.piccy.databinding.FragmentVerificationBinding
import com.example.piccy.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

class VerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val verificationBinding = FragmentVerificationBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        verificationBinding.emailDisplayText.text = profileViewModel.getEmail()

        verificationBinding.resendLink.setOnClickListener {
            lifecycleScope.launch {
                try {
                    profileViewModel.resendVerificationEmail()
                } catch (_: Exception) {
                    toast("Failed to send verification link")
                }
            }
        }

        verificationBinding.button2.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (!profileViewModel.isEmailVerified()) {
                        toast("Email still not verified")
                    }
                } catch (_: Exception) {
                    toast("Failed to check email verification")
                }
            }
        }

        return verificationBinding.root
    }

    private fun toast(msg: String) {
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
    }

}