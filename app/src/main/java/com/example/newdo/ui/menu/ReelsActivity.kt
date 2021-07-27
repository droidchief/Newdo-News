package com.example.newdo.ui.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newdo.databinding.ActivityReelsBinding

class ReelsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReelsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener{
            finish()
        }
    }
}