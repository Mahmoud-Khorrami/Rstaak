package com.example.rstaak.fragment

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F61Adapter
import com.example.rstaak.database.MessageDB
import com.example.rstaak.databinding.F61ChatBinding
import com.example.rstaak.general.MyResult
import com.example.rstaak.model.f61_chat.F61ParentModel
import com.example.rstaak.model.f61_chat.F61ReceiveMessageModel
import com.example.rstaak.model.f61_chat.F61SendMessageModel
import com.example.rstaak.req_res.F61CreateChatRequest
import com.example.rstaak.req_res.F61IsTypingData
import com.example.rstaak.req_res.F61Message
import com.example.rstaak.service.SocketIOService
import com.example.rstaak.viewModel.F61ChatViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@AndroidEntryPoint
class F61Chat : Fragment()
{

    lateinit var binding: F61ChatBinding
    private val args: F61ChatArgs by navArgs()
    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var f61AdapterFactory: F61Adapter.Factory
    lateinit var f61Adapter: F61Adapter
    var models = ArrayList<F61ParentModel>()
    @Inject
    lateinit var f61SendMessageModelFactory: F61SendMessageModel.Factory
    @Inject
    lateinit var f61ReceiveMessageModelFactory: F61ReceiveMessageModel.Factory
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val f61ChatViewModel: F61ChatViewModel by viewModels()
    @Inject
    lateinit var f61CreateChatRequestFactory: F61CreateChatRequest.Factory
    @Inject
    lateinit var f61MessageFactory: F61Message.Factory
    private lateinit var chatId: String
    private lateinit var messageId: String
    private lateinit var message: String
    private lateinit var userId: String
    private lateinit var productId: String
    private lateinit var ownerId : String
    private var running = false
    @Inject
    lateinit var f61IsTypingDataFactory: F61IsTypingData.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f61_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-------------------------------------------

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = f61ChatViewModel
        }

        //-------------------------------------------

        binding.typing.visibility = View.GONE

        //-------------------------------------------

        val chatStatus = args.chatStatus
        chatId = chatStatus.chatId!!
        ownerId = chatStatus.ownerId
        userId = sharedPreferences.getString("userId", null).toString()
        productId = chatStatus.productId

        //-------------------------------------------

        //binding.productTitle.text = chatStatus.productTitle
        //binding.username.text = "کاربر"

        if(chatStatus.productImage != "")
            picasso.load(chatStatus.productImage).error(R.drawable.image).fit().into(binding.productImage)
        //-------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f61Adapter = f61AdapterFactory.create(models, chatId)
        binding.recyclerView.adapter = f61Adapter

        //--------------------------------------------

        f61ChatViewModel.messages.observe(viewLifecycleOwner, {

            if(it.isNotEmpty())
            {
                var position = 0
                models.clear()

                for ((index, item) in it.withIndex())
                {
                    if(item.senderId == userId)
                    {
                        val model = f61SendMessageModelFactory.create(item.messageId, item.senderId, item.message, item.sentDateTime.toString(), item.deliveredDateTime.toString(), item.viewedDateTime.toString())
                        models.add(model)

                        position = index
                    }
                    else
                    {
                        val model = f61ReceiveMessageModelFactory.create(item.messageId, item.senderId, item.message, item.sentDateTime.toString(), item.deliveredDateTime.toString(), item.viewedDateTime.toString())
                        models.add(model)

                        if(item.viewedDateTime != "0") position = index
                    }
                }

                f61Adapter.notifyDataSetChanged()
                binding.recyclerView.scrollToPosition(position)
            }
        })

        //--------------------------------------------

        binding.send.setOnClickListener {

            if(binding.message.text.isNotEmpty())
            {
                messageId = UUID.randomUUID().toString()
                message = binding.message.text.toString()

                val model = f61SendMessageModelFactory.create(messageId, userId, message, "0", "0", "0")
                models.add(model)
                f61Adapter.notifyDataSetChanged()
                binding.recyclerView.scrollToPosition(models.size - 1)

                if(chatStatus.chatId!!.isNotEmpty())
                    sendMessage()

                else
                {
                    val request = f61CreateChatRequestFactory.create(userId, ownerId, productId )
                    f61ChatViewModel.createChat(request)
                }

                binding.message.setText("")

            }
        }

        //--------------------------------------------

        f61ChatViewModel.f61CreateChatResponseData.observe(viewLifecycleOwner, {

            when(it.status)
            {
                MyResult.Status.SUCCESS ->
                {
                    if(it.data?.status == 200)
                    {
                        chatId = it.data.message.chat.id
                        sendMessage()
                    }
                }

                MyResult.Status.ERROR -> Log.i(TAG, "onViewCreated: ${it.error}")
                MyResult.Status.LOADING -> Log.i(TAG, "onViewCreated: Loading")
            }


        })

        //--------------------------------------------

        binding.message.addTextChangedListener(textWatcher)

    }

    override fun onResume()
    {
        super.onResume()

        //-------------------------------------------

        F61ChatVisibility = true
        registerReceivers()

    }

    fun sendMessage()
    {
        val message = f61MessageFactory.create(messageId, chatId, message)

        val intent = Intent(context, SocketIOService::class.java)
        intent.putExtra(SocketIOService.EVENT_TYPE, "send message")
        intent.putExtra(SocketIOService.DATA, message)
        requireActivity().startService(intent)

    }

    private fun registerReceivers()
    {
        val intentFilter1 = IntentFilter("message")
        requireActivity().registerReceiver(broadcastReceiver1, intentFilter1)
        val intentFilter2 = IntentFilter("sent")
        requireActivity().registerReceiver(broadcastReceiver2, intentFilter2)
        val intentFilter3 = IntentFilter("delivered")
        requireActivity().registerReceiver(broadcastReceiver3, intentFilter3)
        val intentFilter4 = IntentFilter("viewed")
        requireActivity().registerReceiver(broadcastReceiver4, intentFilter4)
        val intentFilter5 = IntentFilter("online")
        requireActivity().registerReceiver(broadcastReceiver5, intentFilter5)
        val intentFilter6 = IntentFilter("offline")
        requireActivity().registerReceiver(broadcastReceiver6, intentFilter6)
        val intentFilter7 = IntentFilter("typing")
        requireActivity().registerReceiver(broadcastReceiver7, intentFilter7)
    }

    private var textWatcher: TextWatcher = object : TextWatcher
    {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int)
        {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int)
        {
            if(!running)
            {
                val f61IsTypingData = f61IsTypingDataFactory.create(chatId, userId, "is typing")

                val service = Intent(context, SocketIOService::class.java)
                service.putExtra(SocketIOService.EVENT_TYPE, "typing")
                service.putExtra(SocketIOService.DATA, f61IsTypingData)
                requireActivity().startService(service)
            }
        }

        override fun afterTextChanged(editable: Editable)
        {
            val thread = Thread {
                if(!running)
                {
                    running = true

                    Thread.sleep(2000)

                    val f61IsTypingData = f61IsTypingDataFactory.create(chatId, userId, "stop typing")

                    val service = Intent(context, SocketIOService::class.java)
                    service.putExtra(SocketIOService.EVENT_TYPE, "typing")
                    service.putExtra(SocketIOService.DATA, f61IsTypingData)
                    requireActivity().startService(service)

                    running = false
                }
            }
            thread.start()
        }
    }

    companion object
    {
        private const val TAG = "F61Chat"
        var F61ChatVisibility : Boolean  = false
    }

    private val broadcastReceiver1: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "message")
            {
                val bundle = intent.extras
                if(bundle!!.getString("chatId") == chatId)
                {
                    val model = f61ReceiveMessageModelFactory.create(bundle.getString("messageId")!!, bundle.getString("senderId")!!, bundle.getString("message")!!, bundle.getString("sentDateTime")!!, "0", "0")
                    models.add(model)
                    f61Adapter.notifyDataSetChanged()
                    binding.recyclerView.scrollToPosition(models.size - 1)
                }
            }
        }
    }

    private val broadcastReceiver2: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "sent")
            {
                val bundle = intent.extras
                if(bundle!!.getString("chatId") == chatId)
                {
                    for (i in models.indices)
                    {
                        if(models[i].currentType == F61ParentModel.SendMessage)
                        {
                            val model = models[i] as F61SendMessageModel
                            if(model.messageId == bundle.getString("messageId"))
                            {
                                model.sentDateTime = bundle.getString("sentDateTime").toString()
                                f61Adapter.notifyDataSetChanged()
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private val broadcastReceiver3: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "delivered")
            {
                val bundle = intent.extras
                if(bundle!!.getString("chatId") == chatId)
                {
                    for (i in models.indices)
                    {
                        if(models[i].currentType == F61ParentModel.SendMessage)
                        {
                            val model = models[i] as F61SendMessageModel
                            if(model.messageId == bundle.getString("messageId"))
                            {
                                model.deliveredDateTime = bundle.getString("deliveredDateTime").toString()
                                f61Adapter.notifyDataSetChanged()
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private val broadcastReceiver4: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "viewed")
            {
                val bundle = intent.extras
                if(bundle!!.getString("chatId") == chatId)
                {
                    for (i in models.indices)
                    {
                        if(models[i].currentType == F61ParentModel.SendMessage)
                        {
                            val model = models[i] as F61SendMessageModel
                            if(model.messageId == bundle.getString("messageId"))
                            {
                                model.viewedDateTime = bundle.getString("viewedDateTime").toString()
                                f61Adapter.notifyDataSetChanged()
                                break
                            }
                        }
                        else if(models[i].currentType == F61ParentModel.ReceiveMessage)
                        {
                            val model = models[i] as F61ReceiveMessageModel
                            if(model.messageId == bundle.getString("messageId"))
                            {
                                model.viewedDateTime = bundle.getString("viewedDateTime").toString()
                                f61Adapter.notifyDataSetChanged()
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private val broadcastReceiver5: BroadcastReceiver = object : BroadcastReceiver()
    {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "online")
            {
                val bundle = intent.extras
                if(bundle!!.getString("userId") == ownerId) binding.online.text = "online"
            }
        }
    }

    private val broadcastReceiver6: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "offline")
            {
                val bundle = intent.extras
                if(bundle!!.getString("userId") == ownerId) binding.online.text = ""
            }
        }
    }

    private val broadcastReceiver7: BroadcastReceiver = object : BroadcastReceiver()
    {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent)
        {
            if(intent.action == "typing")
            {
                val bundle = intent.extras
                val s = bundle!!.getString("data")
                try
                {
                    val data = JSONObject(s)
                    if(data.getString("chatId") == chatId)
                    {
                        if(data.getString("status") == "is typing")
                        {
                            binding.online.visibility = View.GONE
                            binding.typing.visibility = View.VISIBLE
                            binding.typing.text = "is typing ..."
                        }
                        else
                        {
                            binding.online.visibility = View.VISIBLE
                            binding.typing.visibility = View.GONE
                            binding.typing.text = ""
                        }
                    }
                } catch (e: JSONException)
                {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onPause()
    {
        super.onPause()

        requireActivity().unregisterReceiver(broadcastReceiver1)
        requireActivity().unregisterReceiver(broadcastReceiver2)
        requireActivity().unregisterReceiver(broadcastReceiver3)
        requireActivity().unregisterReceiver(broadcastReceiver4)
        requireActivity().unregisterReceiver(broadcastReceiver5)
        requireActivity().unregisterReceiver(broadcastReceiver6)
        requireActivity().unregisterReceiver(broadcastReceiver7)

        F61ChatVisibility = false
    }
}