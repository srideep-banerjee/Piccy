package com.example.piccy.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.piccy.databinding.FragmentLoginBinding
import com.example.piccy.view.Util.Companion.addListener
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginFragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)

        val profileViewModel by activityViewModels<ProfileViewModel>()

        loginFragmentBinding.signupLink.setOnClickListener {
            profileViewModel.updateScreen(ProfileScreen.SIGNUP)
        }

        loginFragmentBinding.loginButton.setOnClickListener {

            (this.activity as ProfileActivity).showDialog("Logging in")

            profileViewModel.login { success, msg ->
                if (!success) {
                    Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
                }
                (this.activity as ProfileActivity).hideDialog()
            }
        }

        loginFragmentBinding.emailEditTextLogin.setText(profileViewModel.currentEntries[0])
        loginFragmentBinding.passwordEditTextLogin.setText(profileViewModel.currentEntries[1])

        addListener(loginFragmentBinding.emailEditTextLogin, profileViewModel, 0)
        addListener(loginFragmentBinding.passwordEditTextLogin, profileViewModel, 1)

        return loginFragmentBinding.root
    }

}