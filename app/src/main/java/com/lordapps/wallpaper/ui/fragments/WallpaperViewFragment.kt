package com.lordapps.wallpaper.ui.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.adapter.ViewPagerAdapter
import com.lordapps.wallpaper.databinding.WallpaperViewBinding
import com.lordapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.lordapps.wallpaper.utils.Constants.LATEST_WALLPAPERS_ENDED_INDEX
import com.lordapps.wallpaper.utils.Constants.LATEST_WALLPAPERS_STARTED_INDEX
import com.lordapps.wallpaper.utils.Constants.PREMIUM_WALLPAPERS_STARTED_INDEX
import com.lordapps.wallpaper.utils.RawImagesResult
import com.lordapps.wallpaper.utils.intToBitmap


class WallpaperViewFragment:Fragment (){
    private val viewModel: RawImagesViewModel by activityViewModels()

    private var _binding: WallpaperViewBinding?=null
    private val binding get()=_binding
    private val args:WallpaperViewFragmentArgs by navArgs()
    private var mInterstitialAd: InterstitialAd? = null
    private  var TAG = "WallpaperViewFragment"

    private var wallpapers:List<Int> = listOf()
    private var allImages:List<Int>  = listOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=WallpaperViewBinding.inflate(inflater,container,false)
        return binding?.root
    }

    @SuppressLint("Recycle")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getImages()

        setupInterstitialAds()

        binding?.back?.setOnClickListener {
            clickBack()
        }

        binding?.setWallpaper?.setOnClickListener {
            sendImageForEdit()
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
                mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        showDialog()
                        setupInterstitialAds()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.")
                        showDialog()
                        mInterstitialAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }
                }

            } else {
                showDialog()
            }

        }

        binding?.edit?.setOnClickListener{
            sendImageForEdit()
            findNavController().navigate(R.id.action_wallpaperViewFragment_to_editFragment)
        }

        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        activity?.onBackPressedDispatcher?.addCallback {
            findNavController().popBackStack()
        }
        binding?.right?.setOnClickListener {
            binding?.vpWallpaper?.currentItem = binding?.vpWallpaper?.currentItem!! - 1
        }
        binding?.left?.setOnClickListener {
            binding?.vpWallpaper?.currentItem = binding?.vpWallpaper?.currentItem!! + 1
        }
        binding?.vpWallpaper?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    (wallpapers.size-1) -> {
                        binding?.left?.visibility=View.GONE
                    }
                    0 -> {
                        binding?.right?.visibility=View.GONE
                    }
                    else -> {
                        with(binding){
                            this?.left?.visibility=View.VISIBLE
                            this?.right?.visibility=View.VISIBLE
                        }
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    private fun setupInterstitialAds() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),getString(R.string.inter_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("InterstitialAd", adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("mInterstitialAd", "Ad was loaded")
                mInterstitialAd = interstitialAd
            }
        })

    }


    private fun sendImageForEdit(){
        viewModel.rawId=allImages.indexOf(wallpapers[binding?.vpWallpaper?.currentItem!!])
        viewModel.bitmap= intToBitmap(wallpapers[binding?.vpWallpaper?.currentItem!!],requireContext())
        viewModel.bitmapWallpaper= intToBitmap(wallpapers[binding?.vpWallpaper?.currentItem!!],requireContext())
    }
    private fun getImages() {
        viewModel.loadRawImages()
        viewModel.rawImagesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RawImagesResult.Success -> {
                    // handle the list of image resource IDs
                    val anim = ObjectAnimator.ofFloat(binding?.vpWallpaper, View.ALPHA, 0f, 2f)
                    anim.duration = 700
                    anim.start()
                    allImages = result.images
                    setViewPager(allImages)

                }
                is RawImagesResult.Error -> {
                    // handle the error message
                    val message = result.message
                }
            }
        }
    }
    private fun setViewPager(images: List<Int>) {
        when(args.id){
            R.id.action_wallpaperViewFragment_to_homeFragment ->{
                wallpapers=images.subList(0,PREMIUM_WALLPAPERS_STARTED_INDEX)
                val adapter = ViewPagerAdapter(requireContext(),wallpapers)
                binding?.vpWallpaper?.adapter = adapter
                binding?.vpWallpaper?.currentItem = viewModel.rawId
            }
            R.id.action_wallpaperViewFragment_to_latestFragment -> {
                wallpapers=images.subList(
                    LATEST_WALLPAPERS_STARTED_INDEX,
                    LATEST_WALLPAPERS_ENDED_INDEX
                )
                val adapter = ViewPagerAdapter(requireContext(),wallpapers)
                binding?.vpWallpaper?.adapter = adapter
                binding?.vpWallpaper?.currentItem = viewModel.rawId - LATEST_WALLPAPERS_STARTED_INDEX
            }
            R.id.action_wallpaperViewFragment_to_premiumFragment ->{
                wallpapers=images.subList(
                    PREMIUM_WALLPAPERS_STARTED_INDEX,
                    images.size
                )
                val adapter = ViewPagerAdapter(requireContext(),wallpapers)
                binding?.vpWallpaper?.adapter = adapter
                binding?.vpWallpaper?.currentItem = viewModel.rawId - PREMIUM_WALLPAPERS_STARTED_INDEX
            }
        }

    }
    private fun setHomeWallpaper(){
        viewModel.setWallpaper(requireContext(), viewModel.bitmapWallpaper!!)
        viewModel.setWallpaperSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Wallpaper set successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "Failed to set wallpaper", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setLockScreenWallpaper(){
        viewModel.setLockScreenWallpaper(requireContext(), viewModel.bitmapWallpaper!!)
        viewModel.setWallpaperSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Wallpaper set successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "Failed to set wallpaper", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.alert_dialog, null)
        val setHomeSceen=view.findViewById<LinearLayout>(R.id.set_wallpaper_home)
        val setLockscreen=view.findViewById<LinearLayout>(R.id.set_wallpaper_lock)
        builder.setView(view) // set the view to the AlertDialog

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent);

        setHomeSceen.setOnClickListener {
            setHomeWallpaper()
            alertDialog.dismiss()
        }

        setLockscreen.setOnClickListener {
            setLockScreenWallpaper()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun clickBack(){
        findNavController().popBackStack()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}