package com.example.piccy.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.piccy.databinding.FragmentVerificationBinding
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class VerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val verificationBinding = FragmentVerificationBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        verificationBinding.emailDisplayText.text = profileViewModel.getEmail()

        verificationBinding.resendLink.setOnClickListener {
            profileViewModel.resendVerificationEmail {completed ->
                if (completed) toast("Verification email sent")
                else toast("Failed to send verification email")
            }
        }

        verificationBinding.button2.setOnClickListener {
            profileViewModel.isEmailVerified {
                if (it) profileViewModel.updateScreen(ProfileScreen.DETAILS)
                else toast("Email still not verified")
            }
        }

        return verificationBinding.root
    }

    private fun toast(msg: String) {
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
    }

}