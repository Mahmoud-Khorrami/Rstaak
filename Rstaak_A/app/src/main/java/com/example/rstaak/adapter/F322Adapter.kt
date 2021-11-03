package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f322_shop_select.F322Model
import com.example.rstaak.model.f322_shop_select.F322ParentModel
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class F322Adapter @AssistedInject constructor(
    @Assisted var models: List<F322ParentModel>,
    @Assisted var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    @Inject
    lateinit var picasso: Picasso

    class MainViewHolder(var binding: F322ItemBinding): RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: LoadingBinding): RecyclerView.ViewHolder(binding.root)
    class RetryViewHolder(var binding: RetryBinding): RecyclerView.ViewHolder(binding.root)

    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted models: List<F322ParentModel>,
            @Assisted onItemClickListener: OnItemClickListener): F322Adapter
    }

    interface OnItemClickListener
    {
        fun onItemClick(view: View, model: F322Model? = null)
    }

    override fun getItemViewType(position: Int): Int
    {
        return models[position].currentType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        when (viewType)
        {
            F322ParentModel.Main ->
            {
                val binding = F322ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F322ParentModel.Loading ->
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
            val model = models[position] as F322Model
            holder.itemView.tag = model

            //---------------------------------------------------------

            holder.binding.shop.text = model.title
            if(model.image.isNotEmpty())
                picasso.load(model.image).error(R.drawable.image).fit().into(holder.binding.image)

            //---------------------------------------------------------

            holder.binding.cardView.setOnClickListener {
                onItemClickListener.onItemClick(it, model)
            }
        }

        else if(holder is RetryViewHolder)
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