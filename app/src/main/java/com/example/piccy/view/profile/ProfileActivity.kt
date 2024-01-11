package com.example.piccy.view.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.piccy.R
import com.example.piccy.databinding.ActivityProfileBinding
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private val anonymousFragment: AnonymousFragment by lazy { AnonymousFragment() }
    private val signupFragment: SignupFragment by lazy { SignupFragment() }
    private val loginFragment: LoginFragment by lazy { LoginFragment() }
    private val detailsFragment: DetailsFragment by lazy { DetailsFragment() }
    private val verificationFragment: VerificationFragment by lazy { VerificationFragment() }
    private lateinit var dialog: AlertDialog
    private lateinit var customAlertTitle: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customAlertTitle = LayoutInflater
            .from(this@ProfileActivity)
            .inflate(R.layout.alert_dialog_layout, null)

        dialog = AlertDialog
            .Builder(this@ProfileActivity)
            .setCustomTitle(customAlertTitle)
            .setCancelable(false)
            .create()

        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        val profileViewModel by viewModels<ProfileViewModel>()

//        val manager = supportFragmentManager
//
//        manager.setFragmentResultListener("nextScreen", this){ _, bundle ->
//            val screenName = bundle.getString("screenName")?:ProfileScreen.ANONYMOUS.screenName
//            switchScreenTo(ProfileScreen.getTypeByName(screenName))
//        }

        switchScreenTo(profileViewModel.currentScreen.value?:ProfileScreen.ANONYMOUS)

//        manager.addOnBackStackChangedListener {
//            if(manager.backStackEntryCount == 0) profileViewModel.updateScreen(ProfileScreen.ANONYMOUS)
//            else {
//                val backStackName = manager.getBackStackEntryAt(0).name
//                val screenName = ProfileScreen.getTypeByName(backStackName!!)
//                profileViewModel.updateScreen(screenName)
//            }
//        }

        onBackPressedDispatcher.addCallback {
            when (profileViewModel.currentScreen.value) {
                ProfileScreen.VERIFICATION,
                ProfileScreen.DETAILS,
                ProfileScreen.ANONYMOUS -> finish()
                else -> profileViewModel.updateScreen(ProfileScreen.ANONYMOUS)
            }
        }

        val currentScreenObserver: Observer<ProfileScreen> = Observer {
            switchScreenTo(it)
        }

        profileViewModel.currentScreen.observe(this, currentScreenObserver)
    }

    private fun getFragmentInstanceByType(screen: ProfileScreen): Fragment {
        return when(screen) {
            ProfileScreen.ANONYMOUS -> anonymousFragment
            ProfileScreen.LOGIN -> loginFragment
            ProfileScreen.SIGNUP -> signupFragment
            ProfileScreen.DETAILS -> detailsFragment
            ProfileScreen.VERIFICATION -> verificationFragment
        }
    }

    private fun switchScreenTo(screen: ProfileScreen) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = getFragmentInstanceByType(screen)

        fragmentTransaction.replace(profileBinding.fragmentContainerView2.id, fragment)

//        if(screen == ProfileScreen.ANONYMOUS) {
//            fragmentTransaction.replace(profileBinding.fragmentContainerView2.id, fragment)
//        } else {
//            fragmentManager.popBackStack()
//            fragmentTransaction.addToBackStack(screen.screenName)
//            fragmentTransaction.replace(profileBinding.fragmentContainerView2.id, fragment)
//        }

        fragmentTransaction.commit()
    }

    fun showDialog(msg: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            customAlertTitle.findViewById<TextView>(R.id.alertText).text = msg
            dialog.show()
        }
    }

    fun hideDialog() {
        dialog.dismiss()
    }
}