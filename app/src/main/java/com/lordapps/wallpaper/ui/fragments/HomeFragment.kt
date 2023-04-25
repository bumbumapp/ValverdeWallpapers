package com.lordapps.wallpaper.ui.fragments



import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.adapter.WallpapersAdapter
import com.lordapps.wallpaper.databinding.HomeFragmentBinding
import com.lordapps.wallpaper.ui.listener.OnItemClickListener
import com.lordapps.wallpaper.ui.listener.OnSwipeTouchListener
import com.lordapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.lordapps.wallpaper.utils.Constants.PREMIUM_WALLPAPERS_STARTED_INDEX
import com.lordapps.wallpaper.utils.RawImagesResult


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
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getImages()


        binding?.constraint?.setOnTouchListener(object :OnSwipeTouchListener(requireContext()){
            override fun onSwipeLeft() {
                findNavController().navigate(R.id.action_homeFragment_to_latestFragment)
            }
        })
     activity?.onBackPressedDispatcher?.addCallback {
         requireActivity().finishAffinity()
     }

        binding?.latest?.setOnClickListener {
findNavController().navigate(R.id.action_homeFragment_to_latestFragment)
        }
        binding?.premium?.setOnClickListener {
findNavController().navigate(R.id.action_homeFragment_to_premiumFragment)
        }
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
