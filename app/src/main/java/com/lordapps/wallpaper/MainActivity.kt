package com.lordapps.wallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lordapps.wallpaper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBottomNavVisibility()
    }

    private fun loadBannerAds() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }
    private fun configureBottomNavVisibility() = with(binding) {
        findNavController(R.id.nav_host_fragment_content_main).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    binding.adView.visibility=View.GONE
                }
                R.id.introFragment  -> {
                    binding.adView.visibility=View.GONE
                }
                else -> {
                    binding.adView.visibility=View.VISIBLE
                    loadBannerAds()
                }
            }
        }
    }
}