package com.example.newdo.ui.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.example.newdo.R
import com.example.newdo.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        disableDarkMode()

        binding.back.setOnClickListener {
            finish()
        }

        itemClicks()
    }

    private fun itemClicks() {
        binding.settings.setOnClickListener(this)
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

}