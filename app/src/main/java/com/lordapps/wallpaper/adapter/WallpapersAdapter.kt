package com.lordapps.wallpaper.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.databinding.ItemSmallBinding
import com.lordapps.wallpaper.databinding.WallpapersBinding
import com.lordapps.wallpaper.ui.listener.OnItemClickListener
import com.bumptech.glide.Glide

class WallpapersAdapter(private var images: List<Int>,private val listener: OnItemClickListener) : RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallpaperViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        return if (viewType==0){
            val binding=WallpapersBinding.inflate(inflater,parent,false)
            WallpaperViewHolder(binding)
        } else{
            val binding=ItemSmallBinding.inflate(inflater,parent,false)
            WallpaperViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .load(images[position])
            .into(holder.binding.root.findViewById(R.id.wallpaper))

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position%2
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class WallpaperViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {}
}
