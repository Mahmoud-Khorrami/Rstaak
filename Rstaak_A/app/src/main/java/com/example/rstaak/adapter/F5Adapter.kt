package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.F5ItemBinding
import com.example.rstaak.databinding.LoadingBinding
import com.example.rstaak.databinding.RetryBinding
import com.example.rstaak.model.f4_shop.F4ShopModel
import com.example.rstaak.model.f4_shop.F4ShopParentModel
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class F5Adapter @AssistedInject constructor(
    @Assisted var models: List<F4ShopParentModel>,
    @ActivityContext var context: Context,
    @Assisted var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var picasso: Picasso

    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted modelF4s: List<F4ShopParentModel>,
            @Assisted onItemClickListener: OnItemClickListener): F5Adapter
    }

    interface OnItemClickListener
    {
        fun onItemClick(view: View, shopId: String? = null)
    }

    class MainViewHolder(var binding: F5ItemBinding): RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: LoadingBinding): RecyclerView.ViewHolder(binding.root)
    class RetryViewHolder(var binding: RetryBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int
    {
        return models[position].currentType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        when (viewType)
        {
            F4ShopParentModel.Main ->
            {
                val binding = F5ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F4ShopParentModel.Loading ->
            {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingViewHolder(binding)
            }
            else ->
            {
                val binding = RetryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RetryViewHolder(binding)
            }

        }

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)

        if(holder is MainViewHolder)
        {

            val model = models[position] as F4ShopModel

            //--------------------------------------------------

            holder.binding.title.text = model.title
            holder.binding.description.text = model.description
            holder.binding.address.text = model.address
            holder.binding.likeNumber.text = model.likedNumber.toString()
            holder.binding.viewNumber.text = model.viewedNumber.toString()

            //------------------------------------------------------

            if(model.imageList.isNotEmpty())
                picasso.load(model.imageList[0]).error(R.drawable.image).fit().into(holder.binding.shopImage)

            //------------------------------------------------------

            if(model.likedNumber>0)
                holder.binding.like.setColorFilter(ContextCompat.getColor(context, R.color.red))

            if(model.viewedNumber>0)
                holder.binding.view.setColorFilter(ContextCompat.getColor(context, R.color.black))

            //------------------------------------------------------

            holder.binding.edit.setOnClickListener {
                onItemClickListener.onItemClick(it, shopId = model.shopId)
            }
        }

        if(holder is RetryViewHolder)
        {
            holder.binding.retry.setOnClickListener {
                onItemClickListener.onItemClick(it)
            }
        }
    }

    override fun getItemCount(): Int
    {
        return models.size
    }


}

