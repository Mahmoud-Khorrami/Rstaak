package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f321_location.F321Model
import com.example.rstaak.model.f321_location.F321ParentModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F321Adapter @AssistedInject constructor(
    @Assisted var models: List<F321ParentModel>,
    @Assisted var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    @AssistedFactory
    interface Factory
    {
        fun create(models: List<F321ParentModel>,
            onItemClickListener: OnItemClickListener): F321Adapter
    }

    interface OnItemClickListener
    {
        fun onItemClick(view: View, model: F321Model? = null)
    }

    class MainViewHolder(var binding: F321ItemBinding): RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: LoadingBinding): RecyclerView.ViewHolder(binding.root)
    class RetryViewHolder(var binding: RetryBinding): RecyclerView.ViewHolder(binding.root)
    class NotFoundViewHolder(var binding: NotFoundBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int
    {
        return models[position].currentType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        when (viewType)
        {
            F321ParentModel.Main ->
            {
                val binding = F321ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F321ParentModel.Loading ->
            {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingViewHolder(binding)
            }
            F321ParentModel.Retry ->
            {
                val binding = RetryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RetryViewHolder(binding)
            }
            else ->
            {
                val binding = NotFoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return NotFoundViewHolder(binding)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)

        if(holder is MainViewHolder)
        {
            val model = models[position] as F321Model
            holder.itemView.tag = model

            //---------------------------------------------------------

            when (model.type)
            {
                "ostan" -> holder.binding.city.text = "?????????? ${model.ostanName}"
                "shahrestan" -> holder.binding.city.text = "?????????????? ${model.shahrestanName}"
                "bakhsh" -> holder.binding.city.text = "?????? ${model.bakhshName}"
                "shahr" -> holder.binding.city.text = "?????? ${model.dehshahrName}"
                "dehestan" -> holder.binding.city.text = "???????????? ${model.dehshahrName}"
                "abadi" -> holder.binding.city.text = "?????????? ${model.abadiName}"
            }

            if(model.type == "shahr" || model.type == "abadi")
                holder.binding.next.visibility = View.GONE

            //---------------------------------------------------------

            holder.binding.lnr.setOnClickListener {
                onItemClickListener.onItemClick(it,model)
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