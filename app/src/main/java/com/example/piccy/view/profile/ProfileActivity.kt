package com.example.piccy.view.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.piccy.Constants.cropImageOutputName
import com.example.piccy.R
import com.example.piccy.databinding.ActivityProfileBinding
import com.example.piccy.view.ImageUtil
import com.example.piccy.viewmodels.ProfileScreen
import com.example.piccy.viewmodels.ProfileViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Arrays

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private val anonymousFragment: AnonymousFragment by lazy { AnonymousFragment() }
    private val signupFragment: SignupFragment by lazy { SignupFragment() }
    private val loginFragment: LoginFragment by lazy { LoginFragment() }
    private val detailsFragment: DetailsFragment by lazy { DetailsFragment() }
    private val verificationFragment: VerificationFragment by lazy { VerificationFragment() }
    private lateinit var dialog: AlertDialog
    private lateinit var customAlertTitle: View
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>

    @RequiresApi(Build.VERSION_CODES.P)
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

        this.supportActionBar?.elevation = 0f

        val profileViewModel by viewModels<ProfileViewModel>()

        switchScreenTo(profileViewModel.currentScreen.value ?: ProfileScreen.ANONYMOUS)

        onBackPressedDispatcher.addCallback {
            when (profileViewModel.currentScreen.value) {
                ProfileScreen.VERIFICATION,
                ProfileScreen.DETAILS,
                ProfileScreen.ANONYMOUS -> finish()

                else -> profileViewModel.updateScreen(ProfileScreen.ANONYMOUS)
            }
        }

        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                this@ProfileActivity.lifecycleScope.launch(Dispatchers.IO) {
                    result
                        .getBitmap(this@ProfileActivity)
                        ?.copy(Bitmap.Config.ARGB_8888, true)
                        ?.let {bitmap ->

                            result.uriContent?.path?.let { path ->
                                val croppedImgFile = File(
                                    path.replace(
                                        "my_images",
                                        getExternalFilesDir(null)?.path ?: "/",
                                        true
                                    )
                                )
                                val circularBitmap = ImageUtil.circleBitmap(bitmap)
                                croppedImgFile.outputStream().use {
                                    circularBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                }
                            }

                            result.originalUri?.path?.let { path ->
                                File(
                                    path.replace(
                                        "cached_files",
                                        cacheDir.path,
                                        true
                                    )
                                ).delete()
                            }

                            withContext(Dispatchers.Main) {
                                profileBinding.imageView.setImageURI(result.uriContent)
                                profileBinding.imageView.setColorFilter(Color.TRANSPARENT)
                            }
                            profileViewModel.pfp.postValue(result.uriContent)
                        }
                }
            } else {
                profileViewModel.toast.postValue(result.error?.message)
            }
        }

        profileBinding.floatingActionButton.setOnClickListener {
            cropImage.launch(
                CropImageContractOptions(
                    uri = null,
                    CropImageOptions(
                        imageSourceIncludeGallery = true,
                        imageSourceIncludeCamera = true,
                        cropShape = CropImageView.CropShape.OVAL,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        customOutputUri = File(cacheDir, cropImageOutputName).toUri()
                    )
                )
            )
        }


        val currentScreenObserver: Observer<ProfileScreen> = Observer {
            switchScreenTo(it)
            if (it == ProfileScreen.DETAILS) {
                profileBinding.floatingActionButton.show()
            } else {
                profileBinding.floatingActionButton.hide()
            }
        }

        profileViewModel.currentScreen.observe(this, currentScreenObserver)

        val loadingObserver: Observer<Boolean> = Observer { loading ->
            if (loading) {
                profileBinding.loadingScreen.visibility = View.VISIBLE
            } else {
                profileBinding.loadingScreen.visibility = View.GONE
            }
        }

        profileViewModel.loading.observe(this, loadingObserver)

        val toastObserver: Observer<String> = Observer { toast ->
            if (toast != "")
                Toast.makeText(this, toast, Toast.LENGTH_LONG).show()
        }

        profileViewModel.toast.observe(this, toastObserver)
    }

    private fun getFragmentInstanceByType(screen: ProfileScreen): Fragment {
        return when (screen) {
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