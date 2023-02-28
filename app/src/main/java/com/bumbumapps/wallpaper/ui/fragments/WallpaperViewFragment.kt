package com.bumbumapps.wallpaper.ui.fragments

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
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
import com.bumbumapps.wallpaper.R
import com.bumbumapps.wallpaper.databinding.WallpaperViewBinding
import com.bumbumapps.wallpaper.utils.intToBitmap
import com.bumbumapps.wallpaper.utils.RawImageResult
import com.bumbumapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.bumptech.glide.Glide

class WallpaperViewFragment:Fragment (){
    private val viewModel: RawImagesViewModel by activityViewModels()

    private var _binding: WallpaperViewBinding?=null
    private val binding get()=_binding
    private val args:WallpaperViewFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=WallpaperViewBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadRawImage()
        viewModel.rawImageLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RawImageResult.Success -> {
                    val anim = ObjectAnimator.ofFloat(binding?.wallpaper, View.ALPHA, 0f, 2f)
                    anim.duration = 700
                    anim.start()
                    viewModel.bitmap= intToBitmap(result.image,requireContext())
                    viewModel.bitmapWallpaper= intToBitmap(result.image,requireContext())

                    Glide.with(requireContext())
                        .load(result.image)
                        .centerCrop()
                        .into(binding?.wallpaper!!)
                }
                is RawImageResult.Error -> {

                }
            }

        }

        binding?.back?.setOnClickListener {
             clickBack()
        }

        binding?.setWallpaper?.setOnClickListener {
            showDialog()
        }

        binding?.edit?.setOnClickListener{
            findNavController().navigate(R.id.action_wallpaperViewFragment_to_editFragment)
        }

        binding?.back?.setOnClickListener {
            findNavController().navigate(args.id)
        }
        activity?.onBackPressedDispatcher?.addCallback {
            findNavController().popBackStack()
        }

    }
    private fun setHomeWallpaper(){
        viewModel.setWallpaper(requireContext(), intToBitmap(viewModel.getImageFromRaw(),requireContext()))
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
        viewModel.setLockScreenWallpaper(requireContext(), intToBitmap(viewModel.getImageFromRaw(),requireContext()))
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

        alertDialog.show() // show the AlertDialog


    }


    private fun clickBack(){
       findNavController().popBackStack()

   }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}