package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.*
import com.example.rstaak.model.F6ChatModel
import com.example.rstaak.model.F6ParentModel
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class F6Adapter @AssistedInject constructor(
    @Assisted var models: List<F6ParentModel>,
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
            @Assisted models: List<F6ParentModel>,
            @Assisted onItemClickListener: OnItemClickListener): F6Adapter
    }

    interface OnItemClickListener
    {
        fun onItemClick(f6ChatModel: F6ChatModel)
    }

    class MainViewHolder(var binding: F6ItemBinding): RecyclerView.ViewHolder(binding.root)
    class NotFoundViewHolder(var binding: NotFoundBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int
    {
        return models[position].currentType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return when (viewType)
        {
            F6ParentModel.Main ->
            {
                val binding = F6ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MainViewHolder(binding)
            }
            else ->
            {
                val binding = NotFoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NotFoundViewHolder(binding)
            }

        }

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)

        if(holder is MainViewHolder)
        {

            val model = models[position] as F6ChatModel

            //--------------------------------------------------

            holder.binding.productTitle.text = model.chatDB.productId
            holder.binding.username.text = "کاربر"

            if(model.chatDB.productImage != "")
                picasso.load(model.chatDB.productImage).error(R.drawable.image).fit().into(holder.binding.productImage)

            if(model.unreadMessage > 0)
            {
                holder.binding.unReadMessage.visibility = View.VISIBLE
                holder.binding.unReadMessage.text = model.unreadMessage.toString() + ""
            }
            else holder.binding.unReadMessage.visibility = View.GONE

            //--------------------------------------------------

            holder.binding.lnr.setOnClickListener {
                onItemClickListener.onItemClick(model)
            }

        }

    }

    override fun getItemCount(): Int
    {
        return models.size
    }


}