package com.example.piccy.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.piccy.R
import com.example.piccy.databinding.ActivityMainBinding
import com.example.piccy.viewmodels.MainViewModel
import com.example.piccy.viewmodels.Screen


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        //Get ViewModel object
        val mainViewModel by viewModels<MainViewModel>()

        //Set up navigation bar
        navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        mainBinding.bottomBar.setupWithNavController(navController)
        mainBinding.bottomBar.setOnItemSelectedListener {
            mainViewModel.currentScreen.value = when(it.itemId) {
                R.id.menu_home -> Screen.HOME
                R.id.menu_following -> Screen.FOLLOWING
                R.id.menu_liked -> Screen.LIKES
                else -> {
                    Log.i("MSG", "Unknown menu id selected ${it.title} -> ${it.itemId}")
                    Screen.HOME
                }
            }
            return@setOnItemSelectedListener true
        }

    }

//    val PICK_IMAGE = 1
//    lateinit var f:File
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE) {
//            data?.let {
//                val uri=it.data
//                val iv=findViewById<ImageView>(R.id.imageView)
//                iv.setImageURI(uri)
//                val originalFile=File(getPath(uri))
//                println("hahahahahahahahahahahahahahahahaha old"+originalFile.length()/1024)
//                val iv2=findViewById<ImageView>(R.id.imageView2)
//                lifecycleScope.launch{
//                    Compressor.compress(this@MainActivity,originalFile, Dispatchers.IO){
//                        default()
//                        destination(f)
//                        size(256*1024)
//                    }
//                    println("hahahahahahahahahahahahahahahahaha new"+f.length()/1024)
//                    withContext(Dispatchers.Main){
//                        iv2.setImageBitmap(BitmapFactory.decodeFile(f.path))
//                    }
//                }
//            }
//
//        }
//    }
//
//    fun getPath(uri: Uri?): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(uri!!, projection, null, null, null)
//        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        val path=cursor.getString(column_index)
//        cursor.close()
//        return path
//    }
}