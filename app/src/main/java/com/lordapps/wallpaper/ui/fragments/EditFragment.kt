package com.lordapps.wallpaper.ui.fragments

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lordapps.wallpaper.adapter.EffectsAdapter
import com.lordapps.wallpaper.utils.effectsResults
import com.lordapps.wallpaper.ui.listener.OnEffectsItemClicked
import com.lordapps.wallpaper.utils.rotateImage
import com.lordapps.wallpaper.ui.viewmodel.RawImagesViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.databinding.EditFragmentBinding
import java.io.File
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import java.io.FileOutputStream


class EditFragment:Fragment(),OnEffectsItemClicked {
    private val viewModel: RawImagesViewModel by activityViewModels()

    private var _binding: EditFragmentBinding?=null
    private val binding get() = _binding
    private var mInterstitialAd: InterstitialAd? = null

    private  var TAG = "WallpaperViewFragment"

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

        setupInterstitialAds()

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


        }
        binding?.cancel?.setOnClickListener {
            binding?.filterImage?.visibility=View.VISIBLE
            binding?.rotateImage?.visibility=View.VISIBLE
            binding?.cropImage?.visibility=View.VISIBLE
            if (binding?.recyclerView?.isVisible!!){
                binding?.recyclerView?.visibility=View.GONE
                Glide.with(requireContext())
                    .load(viewModel.bitmap)
                    .into(binding?.wallpaper!!)
                setReycleview()
            }else{
                Glide.with(requireContext())
                    .load(viewModel.bitmapWallpaper)
                    .into(binding?.wallpaper!!)
                viewModel.bitmap=viewModel.bitmapWallpaper
            }


        }
        val cropLauncher = registerForActivityResult(
            CropImageContract()
        ) { result ->
            if (result.isSuccessful) {
                val uri:Uri=result.uriContent!!
                val inputStream =context?.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding?.wallpaper?.setImageBitmap(bitmap)
                viewModel.bitmap=bitmap
                setReycleview()
            }




        }
        binding?.cropImage?.setOnClickListener {
            val inputUri =bitmapToUri(viewModel.bitmap!!)
            cropLauncher.launch(
                options(uri = inputUri) {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                }
            )
        }


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
        viewModel.setWallpaper(requireContext(), viewModel.bitmap!!)
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
        viewModel.setLockScreenWallpaper(requireContext(), viewModel.bitmap!!)
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