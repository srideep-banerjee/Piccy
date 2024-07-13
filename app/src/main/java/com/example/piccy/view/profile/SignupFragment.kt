package com.example.piccy.view.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.piccy.databinding.FragmentSignupBinding
import com.example.piccy.view.Util.Companion.setTextListener
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class SignupFragment : Fragment() {

    private var currentActivity: ProfileActivity? = null

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

            currentActivity?.showDialog("Signing up")

            profileViewModel.signup {success, msg ->
                if (!success) {
                    Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
                }
                currentActivity?.hideDialog()
            }
        }

        val entries = profileViewModel
            .currentAuthScreenEntries as ProfileViewModel.SignInEntries

        signupFragmentBinding.emailEditTextSignup.setText(entries.email)
        signupFragmentBinding.nameEditTextSignup.setText(entries.userName)
        signupFragmentBinding.passwordEditTextSignup.setText(entries.password)

        signupFragmentBinding.emailEditTextSignup.setTextListener {
            entries.email = it
        }
        signupFragmentBinding.nameEditTextSignup.setTextListener {
            entries.userName = it
        }
        signupFragmentBinding.passwordEditTextSignup.setTextListener {
            entries.password = it
        }

        return signupFragmentBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProfileActivity) {
            currentActivity = context
        }
    }

}