package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f41_shop_details.F41Model1
import com.example.rstaak.model.f41_shop_details.F41Model2
import com.example.rstaak.model.f41_shop_details.F41ParentModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class F41Adapter1 @AssistedInject constructor(
    @Assisted var models: List<F41ParentModel>,
    @ActivityContext var context: Context,
    @Assisted var onItemClickListener1: OnItemClickListener1,
    @Assisted var onItemClickListener2: F41Adapter2.OnItemClickListener2): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    @AssistedFactory
    interface Factory
    {
        fun create(
            models: List<F41ParentModel>,
            onItemClickListener1: OnItemClickListener1,
            onItemClickListener2: F41Adapter2.OnItemClickListener2): F41Adapter1
    }

    interface OnItemClickListener1
    {
        fun onItemClick1()
    }

    @Inject
    lateinit var f41Adapter2Factory: F41Adapter2.Factory
    @Inject
    lateinit var f41Model2Factory: F41Model2.Factory

    class MainViewHolder(var binding: F41Item1Binding): RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: LoadingBinding): RecyclerView.ViewHolder(binding.root)
    class RetryViewHolder(var binding: RetryBinding): RecyclerView.ViewHolder(binding.root)
    class ShowCaseViewHolder(var binding: F41Item2Binding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int
    {
        return models[position].currentType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        when (viewType)
        {
            F41ParentModel.Main ->
            {
                val binding = F41Item1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F41ParentModel.Loading ->
            {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingViewHolder(binding)
            }
            F41ParentModel.Retry ->
            {
                val binding = RetryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RetryViewHolder(binding)
            }
            else ->
            {
                val binding = F41Item2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ShowCaseViewHolder(binding)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)

        if(holder is MainViewHolder)
        {
            val model = models[position] as F41Model1
            holder.itemView.tag = model

            //-----------------------------------------

            holder.binding.category.text = model.parentCategoryName

            //-----------------------------------------

            val models2 = ArrayList<F41Model2>()
            holder.binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,true)
            val f41Adapter2 = f41Adapter2Factory.create(models2, onItemClickListener2)
            holder.binding.recyclerView.adapter = f41Adapter2

            //-----------------------------------------

            for (item in model.childs)
            {
                val image = if(item.categoryImage.isNotEmpty()) item.categoryImage[0] else ""
                val f41Model2 = f41Model2Factory.create(model.shopId, image, item.childCategoryId, item.childCategoryName, item.productNumbers)
                models2.add(f41Model2)
            }
            f41Adapter2.notifyDataSetChanged()

        }

        else if(holder is RetryViewHolder)
        {
            holder.binding.retry.setOnClickListener {
                onItemClickListener1.onItemClick1()
            }
        }
    }

    override fun getItemCount(): Int
    {
        return models.size
    }
}