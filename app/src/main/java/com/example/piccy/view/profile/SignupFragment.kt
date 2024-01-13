package com.example.piccy.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.piccy.databinding.FragmentSignupBinding
import com.example.piccy.view.Util.Companion.addListener
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val signupFragmentBinding = FragmentSignupBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        signupFragmentBinding.loginLink.setOnClickListener {
            profileViewModel.updateScreen(ProfileScreen.LOGIN)
        }

        signupFragmentBinding.signupButton.setOnClickListener {

            (this.activity as ProfileActivity).showDialog("Signing up")

            profileViewModel.signup {success, msg ->
                if (!success) {
                    Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
                }
                (this.activity as ProfileActivity).hideDialog()
            }
        }

        signupFragmentBinding.emailEditTextSignup.setText(profileViewModel.currentEntries[0])
        signupFragmentBinding.nameEditTextSignup.setText(profileViewModel.currentEntries[1])
        signupFragmentBinding.passwordEditTextSignup.setText(profileViewModel.currentEntries[2])

        addListener(signupFragmentBinding.emailEditTextSignup, profileViewModel, 0)
        addListener(signupFragmentBinding.nameEditTextSignup, profileViewModel, 1)
        addListener(signupFragmentBinding.passwordEditTextSignup, profileViewModel, 2)

        return signupFragmentBinding.root
    }

}