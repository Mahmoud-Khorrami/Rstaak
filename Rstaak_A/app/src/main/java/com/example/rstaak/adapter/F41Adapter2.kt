package com.example.rstaak.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f41_shop_details.F41Model2
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class F41Adapter2 @AssistedInject constructor(@Assisted var models: List<F41Model2>,
    @Assisted var onItemClickListener2: OnItemClickListener2): RecyclerView.Adapter<F41Adapter2.ViewHolder>()
{

    @AssistedFactory
    interface Factory
    {
        fun create(models: List<F41Model2>, onItemClickListener2: OnItemClickListener2): F41Adapter2
    }

    interface OnItemClickListener2
    {
        fun onItemClick2(categoryId: String, categoryName: String)
    }

    @Inject
    lateinit var picasso: Picasso

    class ViewHolder(var binding: F41Item3Binding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val binding = F41Item3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)
        val model = models[position]

        holder.binding.name.text = model.childCategoryName
        holder.binding.number.text = model.productNumbers.toString()

        if(model.image.isNotEmpty())
            picasso.load(model.image).error(R.drawable.image).fit().into(holder.binding.image)

        holder.binding.cardView.setOnClickListener {
            onItemClickListener2.onItemClick2(model.childCategoryId, model.childCategoryName)
        }
    }

    override fun getItemCount(): Int
    {
        return models.size
    }
}