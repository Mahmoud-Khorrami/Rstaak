package com.example.rstaak.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.ImageItemBinding
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ImageAdapter @AssistedInject constructor(@Assisted var imageList: List<String>): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>()
{
    @AssistedFactory
    interface Factory
    {
        fun create(imageList: List<String>): ImageAdapter
    }

    class ImageViewHolder(var binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder
    {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int)
    {
        if(imageList.isNotEmpty())
            Picasso.get().load(imageList[position]).into(holder.binding.image)
        else
            holder.binding.image.setImageResource(R.drawable.image)

        holder.binding.image.scaleType = ImageView.ScaleType.FIT_XY
    }

    override fun getItemCount(): Int
    {
        return imageList.size
    }
}