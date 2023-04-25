package com.lordapps.wallpaper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.databinding.SplashFragmentBinding
import com.lordapps.wallpaper.utils.DataStoreManager
import com.lordapps.wallpaper.utils.datastore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashFragment: Fragment() {
    private var _binding: SplashFragmentBinding?=null
    private val binding get()=_binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= SplashFragmentBinding.inflate(inflater,container,false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataStoreManager = DataStoreManager(requireContext().datastore)
        lifecycleScope.launch {
             delay(1500)
            if (dataStoreManager.isFirstTime.first()){
                dataStoreManager.setNotFirstTime(false)
                findNavController().navigate(R.id.action_splashFragment_to_introFragment)
            }
            else{
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}