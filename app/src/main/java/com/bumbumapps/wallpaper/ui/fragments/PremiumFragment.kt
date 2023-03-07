package com.bumbumapps.wallpaper.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.bumbumapps.wallpaper.R
import com.bumbumapps.wallpaper.adapter.WallpapersAdapter
import com.bumbumapps.wallpaper.databinding.PremiumFragmentBinding
import com.bumbumapps.wallpaper.ui.listener.OnItemClickListener
import com.bumbumapps.wallpaper.ui.listener.OnSwipeTouchListener
import com.bumbumapps.wallpaper.utils.Constants.PREMIUM_WALLPAPERS_STARTED_INDEX
import com.bumbumapps.wallpaper.utils.RawImagesResult
import com.bumbumapps.wallpaper.ui.viewmodel.RawImagesViewModel

class PremiumFragment:Fragment(),OnItemClickListener {
    private var _binding: PremiumFragmentBinding?=null
    private val binding get()=_binding!!
    private val viewModel: RawImagesViewModel by activityViewModels()

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
        getImages()
        binding.constraint.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {

            override fun onSwipeRight() {
                findNavController().navigate(R.id.action_premiumFragment_to_latestFragment)
            }

        })


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

    private fun getImages() {
        viewModel.loadRawImages()
        viewModel.rawImagesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RawImagesResult.Success -> {
                    // handle the list of image resource IDs
                    val images = result.images

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

    override fun onItemClick(position: Int) {
        viewModel.rawId=position+PREMIUM_WALLPAPERS_STARTED_INDEX
        val action=PremiumFragmentDirections.actionPremiumFragmentToWallpaperViewFragment(R.id.action_wallpaperViewFragment_to_premiumFragment)
        Navigation.findNavController(requireView()).navigate(action)
    }

}