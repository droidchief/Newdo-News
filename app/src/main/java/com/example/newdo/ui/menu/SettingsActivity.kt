package com.example.newdo.ui.menu

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import com.example.newdo.databinding.ActivitySettingsBinding
import com.example.newdo.helperfile.ThemeManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    lateinit var themeManager: ThemeManager
    var currentTheme = 0 //Light

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set theme
        themeManager = ThemeManager(this)
        observeAppTheme()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveAppTheme()

        binding.back.setOnClickListener {
            finish()
        }


        notificationToggle()

    }

    private fun saveAppTheme() {
        binding.themeSwitch.setOnCheckedChangeListener{_, isOn ->
            //set theme to 1(dark) if switched ON else 0(light) if OFF
            currentTheme = if (isOn) 1 else 0

            //insert value into datastore
            GlobalScope.launch {
                themeManager.saveCurrentTheme(currentTheme)
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
                    binding.themeSwitch.isChecked = false
                    Toast.makeText(this, "Theme: Light", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                    binding.themeSwitch.isChecked = true
                    Toast.makeText(this, "Theme: Dark", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    delegate.applyDayNight()
                    binding.themeSwitch.isChecked = false
                }
            }
        })
    }

    private fun notificationToggle() {
        binding.notificationSwitch.setOnCheckedChangeListener { _, isOn ->
            if (isOn) Toast.makeText(this, "Notification: ON", Toast.LENGTH_SHORT)
                .show() else Toast.makeText(this, "Notification: OFF", Toast.LENGTH_SHORT).show()
        }
    }

}