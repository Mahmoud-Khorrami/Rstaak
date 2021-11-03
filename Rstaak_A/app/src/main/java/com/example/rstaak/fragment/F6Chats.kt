package com.example.rstaak.fragment

import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F6Adapter
import com.example.rstaak.databinding.F6ChatsBinding
import com.example.rstaak.model.F5NotFound
import com.example.rstaak.model.F6ChatModel
import com.example.rstaak.model.F6ParentModel
import com.example.rstaak.model.f3_product.F3ChatStatus
import com.example.rstaak.viewModel.F6ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F6Chats : Fragment(), F6Adapter.OnItemClickListener
{
    lateinit var binding: F6ChatsBinding
    var models = ArrayList<F6ParentModel>()
    lateinit var f6Adapter: F6Adapter
    @Inject
    lateinit var f6AdapterFactory: F6Adapter.Factory
    @Inject
    lateinit var f6NotFound: F5NotFound
    private val  f6ChatViewModel: F6ChatViewModel by viewModels()
    @Inject
    lateinit var f6ChatModelFactory: F6ChatModel.Factory
    @Inject
    lateinit var f3ChatStatusFactory: F3ChatStatus.Factory
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f6_chats, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        F6ChatVisibility = true
        userId = sharedPreferences.getString("userId", null).toString()
        //-----------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f6Adapter = f6AdapterFactory.create(models, this)
        binding.recyclerView.adapter = f6Adapter

        //-----------------------------------------

        val intentFilter = IntentFilter("new chat")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)

        //-----------------------------------------

        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        //-----------------------------------------

        f6ChatViewModel.chatList.observe(viewLifecycleOwner, { list ->
            models.clear()
            if(list.isNotEmpty())
            {
                for (item in list)
                {
                    var x = 0
                    for (i in item.messages)
                    {
                        if(i.senderId != userId && i.viewedDateTime == "0")
                            x += 1
                    }
                    val model = f6ChatModelFactory.create(item.chatDB, x)
                    models.add(model)

                }
            }

            else
                models.add(f6NotFound)

            f6Adapter.notifyDataSetChanged()
        })

    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "new chat")
            {
                f6ChatViewModel.getChats()
            }
        }
    }

    override fun onResume()
    {
        super.onResume()

        F6ChatVisibility = true
    }

    companion object
    {
        var F6ChatVisibility = false
    }

    override fun onPause()
    {
        super.onPause()

        F6ChatVisibility = false
    }

    override fun onItemClick(f6ChatModel: F6ChatModel)
    {
        val chat = f6ChatModel.chatDB
        val ownerId = if(chat.users[0] == sharedPreferences.getString("userId", null).toString()) chat.users[1] else chat.users[0]
        val chatStatus = f3ChatStatusFactory.create(chat.chatId, chat.productId, ownerId, chat.productTitle, chat.productImage)
        val action = F6ChatsDirections.actionF6ChatsToF61Chat(chatStatus)
        findNavController().navigate(action)
    }
}