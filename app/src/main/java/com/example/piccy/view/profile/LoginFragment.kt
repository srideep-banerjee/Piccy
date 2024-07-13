package com.example.piccy.view.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.piccy.databinding.FragmentLoginBinding
import com.example.piccy.view.Util.Companion.setTextListener
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class LoginFragment : Fragment() {

    private var currentActivity: ProfileActivity? = null

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

            currentActivity?.showDialog("Logging in")

            profileViewModel.login { success, msg ->
                if (!success) {
                    Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
                }
                currentActivity?.hideDialog()
            }
        }

        val entries = profileViewModel
            .currentAuthScreenEntries as ProfileViewModel.LogInEntries

        loginFragmentBinding.emailEditTextLogin.setText(entries.email)
        loginFragmentBinding.passwordEditTextLogin.setText(entries.password)

        loginFragmentBinding.emailEditTextLogin.setTextListener {
            entries.email = it
        }
        loginFragmentBinding.passwordEditTextLogin.setTextListener {
            entries.password = it
        }

        return loginFragmentBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProfileActivity) {
            currentActivity = context
        }
    }

}