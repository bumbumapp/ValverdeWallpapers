package com.bumbumapps.wallpaper.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumbumapps.wallpaper.R
import com.bumbumapps.wallpaper.utils.RawImagesResult
import com.bumbumapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.bumbumapps.wallpaper.adapter.WallpapersAdapter
import com.bumbumapps.wallpaper.databinding.HomeFragmentBinding
import com.bumbumapps.wallpaper.ui.listener.OnItemClickListener
import com.bumbumapps.wallpaper.utils.Constants.PREMIUM_WALLPAPERS_STARTED_INDEX


class HomeFragment:Fragment(),OnItemClickListener {
    private var _binding: HomeFragmentBinding?=null
    private val binding get()=_binding
    private val viewModel: RawImagesViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=HomeFragmentBinding.inflate(inflater,container,false)
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding?.recyclerView?.layoutManager = layoutManager

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toggle = ActionBarDrawerToggle(activity, binding?.drawerLayout,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding?.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        getImages()
     activity?.onBackPressedDispatcher?.addCallback {
         requireActivity().finishAffinity()
     }

        binding?.latest?.setOnClickListener {
findNavController().navigate(R.id.action_homeFragment_to_latestFragment)
        }
        binding?.premium?.setOnClickListener {
findNavController().navigate(R.id.action_homeFragment_to_premiumFragment)
        }
        // Set up the navigation drawer menu
//        binding.navView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_item1 -> {
//                    // Handle navigation item 1 click
//                    true
//                }
//                R.id.nav_item2 -> {
//                    // Handle navigation item 2 click
//                    true
//                }
//                else -> false
//            }
//        }
    }

    private fun getImages() {
        viewModel.loadRawImages()
        viewModel.rawImagesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RawImagesResult.Success -> {
                    // handle the list of image resource IDs
                    val images = result.images
                    Log.d("IMages",images.size.toString())
                    val allImages=images.subList(0, PREMIUM_WALLPAPERS_STARTED_INDEX)
                    setRecycleView(allImages)

                }
                is RawImagesResult.Error -> {
                    // handle the error message
                    val message = result.message
                }
            }
        }
    }


    private fun setRecycleView(images: List<Int>) {
        val adapter = WallpapersAdapter(images,this)
        binding?.recyclerView?.adapter = adapter

    }


    override fun onItemClick(position: Int) {
        viewModel.rawId=position
        val action=HomeFragmentDirections.actionHomeFragmentToWallpaperViewFragment(R.id.action_wallpaperViewFragment_to_homeFragment)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}