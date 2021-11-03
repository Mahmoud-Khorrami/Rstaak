package com.example.rstaak.adapter

import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.databinding.F32ItemBinding
import com.example.rstaak.general.ImagePicker
import com.example.rstaak.model.F32Model
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@Suppress("DEPRECATION")
class F32Adapter @AssistedInject constructor(
    @Assisted var models: ArrayList<F32Model>,
    @ActivityContext var context: Context,): RecyclerView.Adapter<F32Adapter.Fragment9ViewHolder>()
{

    @AssistedFactory
    interface Factory
    {
        fun create(models: ArrayList<F32Model>): F32Adapter
    }

    @Inject
    lateinit var imagePicker: ImagePicker

    class Fragment9ViewHolder(var binding: F32ItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Fragment9ViewHolder
    {
        val binding = F32ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Fragment9ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Fragment9ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)
        val model = models[position]

        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, model.uri)
        holder.binding.imageView.setImageBitmap(imagePicker.getResizedBitmap(bitmap, 500))
        holder.binding.imageView.scaleType = ImageView.ScaleType.FIT_XY

        holder.binding.imageView.setOnClickListener {
            models.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int
    {
        return models.size
    }
}