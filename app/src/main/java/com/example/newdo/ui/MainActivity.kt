package com.example.newdo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newdo.R
import com.example.newdo.database.ArticleDatabase
import com.example.newdo.databinding.ActivityMainBinding
import com.example.newdo.helperfile.IOnBackPressed
import com.example.newdo.helperfile.ThemeManager
import com.example.newdo.repository.NewsRepository
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.ui.viewmodels.NewsViewModelProviderFactory

/**
 * @author Victor Loveday
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var themeManager: ThemeManager
    var currentTheme = 0 //Light

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set theme
        themeManager = ThemeManager(this)
        observeAppTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //instantiate news repository
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        bottomNavBackground()

        // setup bottom nav
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }

        //feeds icon badge badge
        val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.feedsFragment)
        badge.isVisible = true

    }

    private fun bottomNavBackground() {
        //fix bottom nav background
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.itemIconTintList = null
    }

    private fun observeAppTheme() {
        themeManager.themeFlow.asLiveData().observe(this, {
            currentTheme = it
            when(currentTheme) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    delegate.applyDayNight()
                    window.navigationBarColor = Color.WHITE
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()

                    //change icon to suite dark theme
                    setDarkThemeAssets()
                    window.navigationBarColor = Color.BLACK

                }

                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    delegate.applyDayNight()
                }
            }
        })
    }

    private fun setDarkThemeAssets() {
        binding.bottomNavigationView.setBackgroundResource(R.drawable.bottom_nav_bg_dark)
        binding.bottomNavigationView.menu.getItem(0).setIcon(R.drawable.home_icon_selector_dark)
        binding.bottomNavigationView.menu.getItem(1).setIcon(R.drawable.search_icon_selector_dark)
        binding.bottomNavigationView.menu.getItem(2).setIcon(R.drawable.favourite_icon_selecto_dark)

        binding.bottomDivider.setBackgroundResource(R.color.white_smoke)
    }


}