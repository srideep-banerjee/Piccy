package com.example.piccy.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.example.piccy.databinding.ActivityProfileBinding
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private val anonymousFragment: AnonymousFragment by lazy { AnonymousFragment() }
    private val signupFragment: SignupFragment by lazy { SignupFragment() }
    private val loginFragment: LoginFragment by lazy { LoginFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        val profileViewModel by viewModels<ProfileViewModel>()

        val manager = supportFragmentManager

        manager.setFragmentResultListener("nextScreen", this){ _, bundle ->
            val screenName = bundle.getString("screenName")?:ProfileScreen.ANONYMOUS.screenName
            switchScreenTo(ProfileScreen.getTypeByName(screenName))
        }

        switchScreenTo(profileViewModel.currentScreen)

        manager.addOnBackStackChangedListener {
            if(manager.backStackEntryCount == 0) profileViewModel.updateScreen(ProfileScreen.ANONYMOUS)
            else {
                val backStackName = manager.getBackStackEntryAt(0).name
                val screenName = ProfileScreen.getTypeByName(backStackName!!)
                profileViewModel.updateScreen(screenName)
            }
        }
    }

    private fun getFragmentInstanceByType(screen: ProfileScreen): Fragment {
        return when(screen) {
            ProfileScreen.ANONYMOUS -> anonymousFragment
            ProfileScreen.LOGIN -> loginFragment
            ProfileScreen.SIGNUP -> signupFragment
        }
    }

    private fun switchScreenTo(screen: ProfileScreen) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = getFragmentInstanceByType(screen)

        if(screen == ProfileScreen.ANONYMOUS) {
            fragmentTransaction.replace(profileBinding.fragmentContainerView2.id, fragment)
        } else {
            fragmentManager.popBackStack()
            fragmentTransaction.addToBackStack(screen.screenName)
            fragmentTransaction.replace(profileBinding.fragmentContainerView2.id, fragment)
        }

        fragmentTransaction.commit()
    }
}