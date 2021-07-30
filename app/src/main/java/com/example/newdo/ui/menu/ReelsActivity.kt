package com.example.newdo.ui.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newdo.databinding.ActivityReelsBinding

/**
 * @author Victor Loveday
 */

class ReelsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReelsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }


}