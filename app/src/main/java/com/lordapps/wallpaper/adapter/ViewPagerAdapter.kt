package com.lordapps.wallpaper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.lordapps.wallpaper.databinding.ImageViewBinding
import com.bumptech.glide.Glide
import java.util.*

class ViewPagerAdapter(private val context:Context,private var wallpapers:List<Int>):PagerAdapter(){
    override fun getCount(): Int {
        return wallpapers.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view=== `object` as ImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater=LayoutInflater.from(context)
        val binding=ImageViewBinding.inflate(inflater,container,false)

        Glide.with(context)
            .load(wallpapers[position])
            .into(binding.wallpaper)
         Objects.requireNonNull(container).addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

}