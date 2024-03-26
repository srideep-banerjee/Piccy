package com.example.piccy.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.piccy.R
import com.example.piccy.databinding.ActivityMainBinding
import com.example.piccy.view.profile.ProfileActivity
import com.example.piccy.viewmodels.MainScreen
import com.example.piccy.viewmodels.MainViewModel
import com.google.android.material.elevation.SurfaceColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var searchViewExpansionObserver: Observer<Boolean>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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
                R.id.menu_home -> MainScreen.HOME
                R.id.menu_following -> MainScreen.FOLLOWING
                R.id.menu_liked -> MainScreen.LIKES
                else -> {
                    Log.i(
                        "MSG",
                        "Unknown menu id selected ${navDestination.label} -> ${navDestination.id}"
                    )
                    MainScreen.HOME
                }
            }
            mainViewModel.updateScreen(newScreen)
        }

        //Setup toolbar
        val color = SurfaceColors.SURFACE_2.getColor(this)
        mainBinding.appBarLayout.setBackgroundColor(color)
        setSupportActionBar(mainBinding.toolbar)

        //Registering for activity result
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            mainViewModel.updateIsEmailVerified()
        }

        //Toast observer
        val toastObserver = Observer<String> {
            lifecycleScope.launch(Dispatchers.Main) {
                if (it != "")
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
            }
        }
        mainViewModel.toast.observe(this, toastObserver)
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

        profileIcon?.setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            activityResultLauncher.launch(intent)
            return@setOnMenuItemClickListener true
        }

        return super.onCreateOptionsMenu(menu)
    }
}