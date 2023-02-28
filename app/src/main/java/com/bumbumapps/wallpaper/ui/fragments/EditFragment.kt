package com.bumbumapps.wallpaper.ui.fragments

import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumbumapps.wallpaper.R
import com.bumbumapps.wallpaper.adapter.EffectsAdapter
import com.bumbumapps.wallpaper.databinding.EditFragmentBinding
import com.bumbumapps.wallpaper.utils.effectsResults
import com.bumbumapps.wallpaper.utils.intToBitmap
import com.bumbumapps.wallpaper.ui.listener.OnEffectsItemClicked
import com.bumbumapps.wallpaper.utils.rotateImage
import com.bumbumapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.soundcloud.android.crop.Crop
import java.io.File
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import java.io.FileOutputStream


class EditFragment:Fragment(),OnEffectsItemClicked {
    private val viewModel: RawImagesViewModel by activityViewModels()

    private var _binding:EditFragmentBinding?=null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        _binding=EditFragmentBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
        binding?.rotateImage?.setOnClickListener {
            val bitmap= rotateImage(binding?.wallpaper!!)
            binding?.wallpaper?.setImageBitmap(bitmap)
            viewModel.bitmap=bitmap
            setReycleview()

        }
        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack(R.id.wallpaperViewFragment,false)
        }
        activity?.onBackPressedDispatcher?.addCallback {
            findNavController().popBackStack()
        }
        binding?.filterImage?.setOnClickListener {
            binding?.filterImage?.visibility=View.GONE
            binding?.rotateImage?.visibility=View.GONE
            binding?.cropImage?.visibility=View.GONE
            binding?.recyclerView?.visibility=View.VISIBLE

        }

        binding?.confirm?.setOnClickListener {
            binding?.filterImage?.visibility=View.VISIBLE
            binding?.rotateImage?.visibility=View.VISIBLE
            binding?.cropImage?.visibility=View.VISIBLE
            if (binding?.recyclerView?.isVisible!!){
                binding?.recyclerView?.visibility=View.GONE
                viewModel.bitmap=(binding?.wallpaper!!.drawable as BitmapDrawable).bitmap
                setReycleview()
            }
            else{
                showDialog()
            }


        }
        binding?.cancel?.setOnClickListener {
            Glide.with(requireContext())
                .load(viewModel.bitmapWallpaper)
                .into(binding?.wallpaper!!)
            viewModel.bitmap=viewModel.bitmapWallpaper

        }
        val cropLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                        val uri:Uri=getOutputUri()
                        val inputStream =context?.contentResolver?.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding?.wallpaper?.setImageBitmap(bitmap)
                        viewModel.bitmap=bitmap
                       setReycleview()

            }




        }
        binding?.cropImage?.setOnClickListener {
                val inputUri =bitmapToUri(viewModel.bitmap!!)
                val outputUri = getOutputUri()// get the URI to save the cropped image
                val intent=Crop.of(inputUri, outputUri).asSquare().getIntent(context)
                cropLauncher.launch(intent)
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
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

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
    private fun setReycleview() {
        val layoutManager = StaggeredGridLayoutManager(1, RecyclerView.HORIZONTAL)
        binding?.recyclerView?.layoutManager = layoutManager
        val adapter = EffectsAdapter(requireContext(), viewModel.bitmap!!,this)
        binding?.recyclerView?.adapter = adapter
    }

    private fun getOutputUri(): Uri {
        val cacheDir: File = context?.cacheDir!!
        val fileName = "wallpaper.jpg"
        val file = File(cacheDir, fileName)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(requireContext(), context?.applicationContext?.packageName+".provider", file)
        } else {
            Uri.fromFile(file)
        }

    }
    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val file = File(context?.cacheDir, "image.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return FileProvider.getUriForFile(
            requireContext(),
            context?.applicationContext?.packageName + ".provider",
            file
        )
    }
    private fun loadImage() {
        val anim = ObjectAnimator.ofFloat(binding?.wallpaper, View.ALPHA, 0f, 2f)
                    anim.duration = 700
                    anim.start()
                    Glide.with(requireContext())
                        .load(viewModel.bitmap)
                        .into(binding?.wallpaper!!)



        setReycleview()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onEffectItemClicked(position:Int) {
            Glide.with(requireContext())
                .load(viewModel.bitmap)
                .apply(RequestOptions.bitmapTransform(ColorFilterTransformation(ContextCompat.getColor(requireContext(),
                    effectsResults()[position]))))
                .into(binding?.wallpaper!!)



    }
}