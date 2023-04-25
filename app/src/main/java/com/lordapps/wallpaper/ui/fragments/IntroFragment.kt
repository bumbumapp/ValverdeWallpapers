package com.lordapps.wallpaper.ui.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.databinding.IntroScreenBinding

class IntroFragment:Fragment() {
    private var _binding: IntroScreenBinding?=null
    private val binding get()=_binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= IntroScreenBinding.inflate(inflater,container,false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)

        binding.getStarted.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_homeFragment)

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}