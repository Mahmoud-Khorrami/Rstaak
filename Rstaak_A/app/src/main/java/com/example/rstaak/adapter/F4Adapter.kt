package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f4_shop.F4ShopModel
import com.example.rstaak.model.f4_shop.F4ShopParentModel
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class F4Adapter @AssistedInject constructor(
    @Assisted var models: List<F4ShopParentModel>,
    @ActivityContext var context: Context,
    @Assisted var onItemClickListener: OnItemClickListener,
    @Assisted recyclerView: RecyclerView,
    @Assisted onLoadMoreListener: OnLoadMoreListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var picasso: Picasso
    private var isLoading = false

    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted modelF4s: List<F4ShopParentModel>,
            @Assisted onItemClickListener: OnItemClickListener,
            @Assisted recyclerView: RecyclerView,
            @Assisted onLoadMoreListener: OnLoadMoreListener): F4Adapter
    }

    interface OnLoadMoreListener
    {
        fun onLoadMore()
    }

    interface OnItemClickListener
    {
        fun onItemClick(view: View, model: F4ShopModel? = null, position: Int? = null, likeFlag: Boolean? = null, clickFlag: String? = null)
    }

    init
    {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                val total = linearLayoutManager.itemCount
                val lastVisible: Int = linearLayoutManager.findLastVisibleItemPosition()

                if(!isLoading and (lastVisible == total - 1) and (total>1))
                {
                    onLoadMoreListener.onLoadMore()
                    isLoading = true
                }
            }
        })
    }

    fun setLoading(b: Boolean)
    {
        isLoading = b
    }

    class MainViewHolder(var binding: F4ItemBinding): RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: LoadingBinding): RecyclerView.ViewHolder(binding.root)
    class RetryViewHolder(var binding: RetryBinding): RecyclerView.ViewHolder(binding.root)
    class NoMoreDataViewHolder(var binding: NoMoreDataBinding): RecyclerView.ViewHolder(binding.root)

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
                val binding = F4ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F4ShopParentModel.Loading ->
            {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingViewHolder(binding)
            }
            F4ShopParentModel.Retry ->
            {
                val binding = RetryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RetryViewHolder(binding)
            }
            else ->
            {
                val binding = NoMoreDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return NoMoreDataViewHolder(binding)
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
            holder.binding.shopsModel = model
            holder.binding.executePendingBindings()

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

            if(model.likeFlag)
                holder.binding.like.setColorFilter(ContextCompat.getColor(context, R.color.red))

            //------------------------------------------------------

            holder.binding.cardView.setOnLongClickListener {

                if(!model.likeFlag)
                {
                    holder.binding.like.setColorFilter(ContextCompat.getColor(context, R.color.red))
                    holder.binding.likeNumber.text = "${holder.binding.likeNumber.text.toString().toInt() + 1}"
                    model.likeFlag = true
                    model.likedNumber += 1
                    onItemClickListener.onItemClick(it, model, likeFlag = true, clickFlag = "Long")
                }
                else
                {
                    holder.binding.like.setColorFilter(ContextCompat.getColor(context, R.color.gray1))
                    holder.binding.likeNumber.text = "${holder.binding.likeNumber.text.toString().toInt() - 1}"
                    model.likeFlag = false
                    model.likedNumber -= 1
                    onItemClickListener.onItemClick(it, model, likeFlag = false, clickFlag = "Long")
                }

                return@setOnLongClickListener true
            }

            //------------------------------------------------------

            holder.binding.cardView.setOnClickListener{
                holder.binding.viewNumber.text = "${holder.binding.viewNumber.text.toString().toInt() + 1}"
                model.viewedNumber += 1
                onItemClickListener.onItemClick(it,clickFlag = "Short", model = model)
            }
        }

        if(holder is RetryViewHolder)
        {
            holder.binding.retry.setOnClickListener {
                onItemClickListener.onItemClick(it, position = position)
            }
        }
    }

    override fun getItemCount(): Int
    {
        return models.size
    }


}

