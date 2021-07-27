package com.example.newdo.ui.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import com.example.newdo.R
import com.example.newdo.databinding.ActivityMenuBinding
import com.example.newdo.helperfile.ThemeManager

class MenuActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMenuBinding

    private lateinit var themeManager: ThemeManager
    var currentTheme = 0 //Light

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set theme
        themeManager = ThemeManager(this)
        observeAppTheme()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        itemClicks()
    }

    private fun itemClicks() {
        binding.settings.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    private fun observeAppTheme() {
        themeManager.themeFlow.asLiveData().observe(this, {
            currentTheme = it
            when(currentTheme) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    delegate.applyDayNight()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                }

                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    delegate.applyDayNight()
                }
            }
        })
    }


}