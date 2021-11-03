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
import com.example.rstaak.model.f3_product.F3ProductModel
import com.example.rstaak.model.f3_product.F3ProductParentModel
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.DecimalFormat
import javax.inject.Inject

class F3Adapter @AssistedInject constructor(
    @Assisted var models: List<F3ProductParentModel>,
    @ActivityContext var context: Context,
    @Assisted var onItemClickListener: OnItemClickListener,
    @Assisted recyclerView: RecyclerView,
    @Assisted onLoadMoreListener: OnLoadMoreListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var df: DecimalFormat
    @Inject
    lateinit var picasso: Picasso
    private var isLoading = false

    interface OnLoadMoreListener
    {
        fun onLoadMore()
    }

    interface OnItemClickListener
    {
        fun onItemClick(view: View, model: F3ProductModel? = null, position: Int? = null, likeFlag: Boolean? = null, clickFlag: String? = null)
    }

    @AssistedFactory
    interface Factory
    {
        fun create(f3ProductModels: List<F3ProductParentModel>, onItemClickListener: OnItemClickListener, recyclerView: RecyclerView, onLoadMoreListener: OnLoadMoreListener): F3Adapter
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

    class MainViewHolder(var binding: F3ItemBinding): RecyclerView.ViewHolder(binding.root)
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
            F3ProductParentModel.Main ->
            {
                val binding = F3ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MainViewHolder(binding)
            }
            F3ProductParentModel.Loading ->
            {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingViewHolder(binding)
            }
            F3ProductParentModel.Retry ->
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

            val model = models[position] as F3ProductModel
            holder.binding.productModel = model
            holder.binding.executePendingBindings()

            //------------------------------------------------------

            if(model.owner == sharedPreferences.getString("userId", null))
                holder.binding.chat.visibility = View.GONE

            //------------------------------------------------------

            holder.binding.price.text = "${df.format(model.price)} تومان "

            //------------------------------------------------------

            if(model.imageList.isNotEmpty())
                picasso.load(model.imageList[0]).error(R.drawable.image).fit().into(holder.binding.image)

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

            //------------------------------------------------------

            holder.binding.chat.setOnClickListener {
                onItemClickListener.onItemClick(view = it, model = model)
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

