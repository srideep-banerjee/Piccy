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
    private lateinit var anonymousFragment: AnonymousFragment
    private lateinit var signupFragment: SignupFragment
    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        anonymousFragment = AnonymousFragment()
        signupFragment = SignupFragment()
        loginFragment = LoginFragment()

        val profileViewModel by viewModels<ProfileViewModel>()

        val screenObserver = Observer<ProfileScreen>{

            switchScreenTo(it)
        }

        val manager = supportFragmentManager
        manager.addOnBackStackChangedListener {
            val name = ProfileScreen.getTypeByName(manager.getBackStackEntryAt(0).name?:ProfileScreen.ANONYMOUS.screenName)
            profileViewModel.currentScreen.value = name?:ProfileScreen.ANONYMOUS
        }

        profileViewModel.currentScreen.observe(this, screenObserver)

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