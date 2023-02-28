package com.bumbumapps.wallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumbumapps.wallpaper.databinding.ActivityMainBinding
import com.bumbumapps.wallpaper.databinding.HomeFragmentBinding
import com.bumbumapps.wallpaper.databinding.SplashFragmentBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}