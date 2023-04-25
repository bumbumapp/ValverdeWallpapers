package com.lordapps.wallpaper.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.adapter.WallpapersAdapter
import com.lordapps.wallpaper.databinding.PremiumFragmentBinding
import com.lordapps.wallpaper.ui.listener.OnItemClickListener
import com.lordapps.wallpaper.ui.listener.OnSwipeTouchListener
import com.lordapps.wallpaper.utils.Constants.PREMIUM_WALLPAPERS_STARTED_INDEX
import com.lordapps.wallpaper.utils.RawImagesResult
import com.lordapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.lordapps.wallpaper.utils.PREMIUM_SHOWED

class PremiumFragment:Fragment(),OnItemClickListener {
    private var _binding: PremiumFragmentBinding?=null
    private val binding get()=_binding!!
    private val viewModel: RawImagesViewModel by activityViewModels()
    private var rewardedAd: RewardedAd? = null
    private  var TAG = "PremiumFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= PremiumFragmentBinding.inflate(inflater,container,false)
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        return binding.root


    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRewardAds()

        if (PREMIUM_SHOWED){
            getImages()
        }




        binding.constraint.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeRight() {
                findNavController().navigate(R.id.action_premiumFragment_to_latestFragment)
            }
        })


        binding.watchVideo.setOnClickListener {
            showRewardVideo()
        }

        binding.all.setOnClickListener {
            findNavController().navigate(R.id.action_premiumFragment_to_homeFragment)
        }
        binding.latest.setOnClickListener {
            findNavController().navigate(R.id.action_premiumFragment_to_latestFragment)

        }
        activity?.onBackPressedDispatcher?.addCallback {
            findNavController().popBackStack()
        }

    }

    private fun showRewardVideo() {
        rewardedAd?.let { ad ->
            ad.show(requireActivity(), OnUserEarnedRewardListener {
                getImages()
            })
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    private fun getImages() {
        viewModel.loadRawImages()
        viewModel.rawImagesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RawImagesResult.Success -> {
                    // handle the list of image resource IDs
                    val images = result.images
                    binding.watchVideo.visibility=View.GONE
                    PREMIUM_SHOWED=true
                    val subLatestImages=images.subList(
                        PREMIUM_WALLPAPERS_STARTED_INDEX,
                        images.size
                    )
                    setRecycleView(subLatestImages)
                }
                is RawImagesResult.Error -> {
                    val message = result.message
                }
            }
        }
    }


    private fun setRecycleView(images: List<Int>) {
        val adapter = WallpapersAdapter(images,this)
        binding.recyclerView.adapter = adapter

    }
    private fun initializeRewardAds() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(),getString(R.string.rewarded_video_id), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
            }
        })
    }

    override fun onItemClick(position: Int) {
        viewModel.rawId=position+PREMIUM_WALLPAPERS_STARTED_INDEX
        val action=PremiumFragmentDirections.actionPremiumFragmentToWallpaperViewFragment(R.id.action_wallpaperViewFragment_to_premiumFragment)
        Navigation.findNavController(requireView()).navigate(action)
    }

}