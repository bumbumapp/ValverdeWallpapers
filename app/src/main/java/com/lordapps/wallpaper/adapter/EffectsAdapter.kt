package com.lordapps.wallpaper.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lordapps.wallpaper.databinding.AdapterEffectsBinding
import com.lordapps.wallpaper.utils.effectsResults
import com.lordapps.wallpaper.ui.listener.OnEffectsItemClicked
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.ColorFilterTransformation

class EffectsAdapter(private var context: Context, private var bitmap: Bitmap, private var listener: OnEffectsItemClicked) : RecyclerView.Adapter<EffectsAdapter.EffectsViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EffectsViewHolder {
        val inflater= LayoutInflater.from(parent.context)
            val binding=AdapterEffectsBinding.inflate(inflater,parent,false)
           return EffectsViewHolder(binding)


    }

    override fun onBindViewHolder(holder: EffectsViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .load(bitmap)
            .apply(RequestOptions.bitmapTransform(ColorFilterTransformation(ContextCompat.getColor(context, effectsResults()[position]))))
            .into(holder.binding.wallpaper)

        holder.itemView.setOnClickListener {
            listener.onEffectItemClicked(position)
        }


    }


    override fun getItemCount(): Int {
        return effectsResults().size
    }

    class EffectsViewHolder(val binding: AdapterEffectsBinding): RecyclerView.ViewHolder(binding.root) {}
}
