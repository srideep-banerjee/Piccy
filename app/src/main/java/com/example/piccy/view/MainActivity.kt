package com.example.piccy.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.piccy.R
import com.example.piccy.databinding.ActivityMainBinding
import com.example.piccy.viewmodels.MainViewModel
import com.example.piccy.viewmodels.Screen


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var searchViewExpansionObserver: Observer<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        //Get ViewModel object
        val mainViewModel by viewModels<MainViewModel>()
        this.mainViewModel = mainViewModel

        //Set up navigation bar
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        //Merge Navigation Controller with Bottom Navigation View
        mainBinding.bottomBar.setupWithNavController(navController)

        //Update current screen in view model
        navController.addOnDestinationChangedListener { _, navDestination: NavDestination, _ ->
            val newScreen = when (navDestination.id) {
                R.id.menu_home -> Screen.HOME
                R.id.menu_following -> Screen.FOLLOWING
                R.id.menu_liked -> Screen.LIKES
                else -> {
                    Log.i(
                        "MSG",
                        "Unknown menu id selected ${navDestination.label} -> ${navDestination.id}"
                    )
                    Screen.HOME
                }
            }
            mainViewModel.updateScreen(newScreen)
        }

        //Setup toolbar
        setSupportActionBar(mainBinding.toolbar)

    }

    //Under changes
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("MSG", "On Create Options Menu called")
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        val searchViewMenu = menu?.findItem(R.id.app_bar_search)
        val profileIcon = menu?.findItem(R.id.self_profile_icon)
        val searchView = searchViewMenu?.actionView as SearchView

        //Configure search view to take up entire space in action bar
        searchView.maxWidth = Int.MAX_VALUE

        //if observer already added aka menu recreated, remove previous observer
        if(this::searchViewExpansionObserver.isInitialized && mainViewModel.searchViewExpanded.hasActiveObservers()) {
            mainViewModel.searchViewExpanded.removeObserver(searchViewExpansionObserver)
            Log.i("MSG", "Observer already present, previous observer removed.")
        }
        searchViewExpansionObserver = Observer { isExpanded ->
            if (isExpanded != searchViewMenu.isActionViewExpanded) {
                if (isExpanded) {
                    searchViewMenu.expandActionView()
                    profileIcon?.isVisible = false
                } else {
                    searchViewMenu.collapseActionView()
                    profileIcon?.isVisible = true
                }
            }
        }

        mainViewModel.searchViewExpanded.observe(this, searchViewExpansionObserver)

        searchViewMenu.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                mainViewModel.searchViewExpanded.value = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mainViewModel.searchViewExpanded.value = false
                return true
            }

        })

        searchView.setQuery(mainViewModel.searchQueryText.value, false)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.updateSearchQueryText(newText?:"")
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
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