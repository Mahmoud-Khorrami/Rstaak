package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f323_category_select.F323Model
import com.example.rstaak.model.f323_category_select.F323ParentModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F323Adapter @AssistedInject constructor(
    @Assisted var models: List<F323ParentModel>,
    @Assisted var onItemClickListener: OnItemClickListener,
    @Assisted var setViewListener: SetViewListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    @AssistedFactory
    interface Factory
    {
        fun create(
            models: List<F323ParentModel>,
            onItemClickListener: OnItemClickListener,
            setViewListener: SetViewListener): F323Adapter
    }

    interface SetViewListener
    {
        fun set(text: String)
    }

    interface OnItemClickListener
    {
        fun onItemClick(view: View, model: F323Model? = null)
    }

    class MainViewHolder(var binding: F323ItemBinding): RecyclerView.ViewHolder(binding.root)
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
            F323ParentModel.Main ->
            {
                val binding = F323ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F323ParentModel.Loading ->
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
            val model = models[position] as F323Model
            holder.itemView.tag = model

            //---------------------------------------------------------

            holder.binding.category.text = model.title

            if(model.child)
            {
                setViewListener.set(model.parentName.toString())
                holder.binding.next.visibility = View.GONE
            }

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