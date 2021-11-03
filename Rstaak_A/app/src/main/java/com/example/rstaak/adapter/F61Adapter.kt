package com.example.rstaak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.R
import com.example.rstaak.databinding.*
import com.example.rstaak.model.f61_chat.F61ParentModel
import com.example.rstaak.model.f61_chat.F61ReceiveMessageModel
import com.example.rstaak.model.f61_chat.F61SendMessageModel
import com.example.rstaak.req_res.F61MessageViewed
import com.example.rstaak.service.SocketIOService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.*
import javax.inject.Inject

class F61Adapter @AssistedInject constructor(
    @Assisted var models: List<F61ParentModel>,
    @ActivityContext var context: Context,
    @Assisted var chatId: String): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    @AssistedFactory
    interface Factory
    {
        fun create(
            models: List<F61ParentModel>,
            chatId: String): F61Adapter
    }

    @Inject
    lateinit var f61MessageViewedFactory: F61MessageViewed.Factory

    class SendMessageViewHolder(var binding: F61Item1Binding): RecyclerView.ViewHolder(binding.root)
    class ReceiveMessageViewHolder(var binding: F61Item2Binding): RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: LoadingBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int
    {
        return models[position].currentType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        when (viewType)
        {
            F61ParentModel.SendMessage ->
            {
                val binding = F61Item1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                return SendMessageViewHolder(binding)
            }
            F61ParentModel.ReceiveMessage ->
            {
                val binding = F61Item2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ReceiveMessageViewHolder(binding)
            }
            else ->
            {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingViewHolder(binding)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        holder.setIsRecyclable(false)

        if(holder is SendMessageViewHolder)
        {
            val model = models[position] as F61SendMessageModel

            Log.i(TAG, "onBindViewHolder: ${model.viewedDateTime}")
            //--------------------------------------------------
            when
            {
                model.viewedDateTime != "0" -> holder.binding.tick.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.viewed_tick))
                model.deliveredDateTime != "0" -> holder.binding.tick.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.delivered_tick))
                model.sentDateTime != "0" -> holder.binding.tick.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.sent_tick))
            }

            //--------------------------------------------------
            holder.binding.message.text = model.message

            if(model.sentDateTime != "0")
            {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = model.sentDateTime.toLong()

                var hour = calendar[Calendar.HOUR_OF_DAY].toString() + ""
                if(hour.toInt() < 10) hour = "0$hour"

                var minute = calendar[Calendar.MINUTE].toString() + ""
                if(minute.toInt() < 10) minute = "0$minute"

                holder.binding.dateTime.text = "$hour:$minute"
            }
        }

        else if(holder is ReceiveMessageViewHolder)
        {
            val model = models[position] as F61ReceiveMessageModel


            //--------------------------------------------------
            holder.binding.message.text = model.message

            if(model.sentDateTime != "0")
            {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = model.sentDateTime.toLong()
                var hour = calendar[Calendar.HOUR_OF_DAY].toString() + ""
                if(hour.toInt() < 10) hour = "0$hour"
                var minute = calendar[Calendar.MINUTE].toString() + ""
                if(minute.toInt() < 10) minute = "0$minute"
                holder.binding.dateTime.text = "$hour:$minute"
            }

            //--------------------------------------------------

            if(model.viewedDateTime == "0")
            {
                val viewedDateTime = Date().time
                model.viewedDateTime = viewedDateTime.toString()

                val data = f61MessageViewedFactory.create(model.messageId, chatId, model.senderId, viewedDateTime.toString())
                val intent = Intent(context, SocketIOService::class.java)
                intent.putExtra(SocketIOService.EVENT_TYPE, "viewed")
                intent.putExtra(SocketIOService.DATA, data)
                context.startService(intent)
            }
        }
    }

    override fun getItemCount(): Int
    {
        return models.size
    }

    companion object
    {
        private const val TAG = "F61Adapter"
    }
}