package com.example.piccy

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.piccy.databinding.ActivityMainBinding
import nl.joery.animatedbottombar.AnimatedBottomBar


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        Log.i("MSG", "Hello world")

        navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        setupActionBarWithNavController(navController)
        supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("MSG", "On create options menu called")
        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        var bottom_bar = mainBinding.bottomBar
        bottom_bar.setupWithNavController(menu!!, navController)
        return true
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