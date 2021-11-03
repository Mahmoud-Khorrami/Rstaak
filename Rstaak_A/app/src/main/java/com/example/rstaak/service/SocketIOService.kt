package com.example.rstaak.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import com.example.rstaak.database.ChatDB
import com.example.rstaak.database.MessageDB
import com.example.rstaak.fragment.F61Chat
import com.example.rstaak.fragment.F6Chats
import com.example.rstaak.general.Constants
import com.example.rstaak.general.MyResult
import com.example.rstaak.general.RstaakUtils
import com.example.rstaak.repository.F61ChatRepository
import com.example.rstaak.req_res.F61IsTypingData
import com.example.rstaak.req_res.F61Message
import com.example.rstaak.req_res.F61MessageViewed
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class SocketIOService : Service(), SocketEventListener.Listener
{

    private lateinit var listenersMap: ConcurrentHashMap<String, SocketEventListener>
    private lateinit var messageQueue: Queue<JSONObject>
    private lateinit var deliveredQueue: Queue<JSONObject>
    private lateinit var viewedQueue: Queue<JSONObject>
    private lateinit var looper: Looper
    private lateinit var serviceHandler: ServiceHandler
    private lateinit var socket: Socket
    @Inject
    lateinit var messageDBFactory: MessageDB.Factory
    @Inject
    lateinit var f61ChatRepository: F61ChatRepository
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var scope = CoroutineScope(Dispatchers.IO + Job())
    private var userId = ""
    @Inject
    lateinit var rstaakUtils: RstaakUtils
    @Inject
    lateinit var intent: Intent
    @Inject
    lateinit var chatDBFactory: ChatDB.Factory

    private class ServiceHandler(looper: Looper?) : Handler(looper!!)
    {
        override fun handleMessage(msg: Message)
        {
            when (msg.arg1)
            {
                1 -> Log.i(TAG, "handleMessage: Connected")
                2 -> Log.i(TAG, "handleMessage: Disconnected")
                3 -> Log.i(TAG, "handleMessage: Error in Connection")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }


    override fun onCreate()
    {
        super.onCreate()

        Log.i(TAG, "onCreate: ok")
        //-----------------------------------------
        listenersMap = ConcurrentHashMap()
        messageQueue = LinkedList()
        deliveredQueue = LinkedList()
        viewedQueue = LinkedList()

        //------------------------------

        val thread = HandlerThread(TAG + "Args", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        looper = thread.looper
        serviceHandler = ServiceHandler(looper)

        userId = sharedPreferences.getString("userId", null).toString()
        //-----------------------------------------

        socket = IO.socket(Constants.CHAT_BASE_URL)
        getSocketListener()

        for ((key, value) in listenersMap)
        {
            socket.on(key, value)
        }

        socket.connect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {

        if(intent != null)
        {
            val bundle = intent.extras

            when(bundle!!.getString(EVENT_TYPE))
            {
                "join" ->
                {
                    if(!socket.connected())
                    {
                        socket.connect()
                        Log.i(TAG, "onStartCommand: connecting socket...")
                    }
                    else
                    {
                        joinChat()
                    }
                }

                "send message" ->
                {
                    val data = bundle.getParcelable<F61Message>(DATA)
                    val messageDB = messageDBFactory.create(data!!.messageId, data.chatId, userId, data.message)

                    scope.launch {
                        f61ChatRepository.saveMessage(messageDB)
                    }

                    val message = JSONObject()
                    message.put("messageId", data.messageId)
                    message.put("chatId", data.chatId)
                    message.put("senderId", userId)
                    message.put("message", data.message)

                    messageQueue.add(message)
                    if(isSocketConnected())
                        resendQueueMessages()
                }

                "viewed" ->
                {
                    val data = bundle.getParcelable<F61MessageViewed>(DATA)

                    scope.launch {
                        f61ChatRepository.updateMessageViewedDataTime(data!!.messageId, data.viewedDateTime)
                    }

                    val message = JSONObject()
                    message.put("messageId", data!!.messageId)
                    message.put("chatId", data.chatId)
                    message.put("senderId", data.senderId)
                    message.put("viewedDateTime", data.viewedDateTime)
                    message.put("user", userId)

                    viewedQueue.add(message)
                    if(isSocketConnected())
                        resendQueueViewed()

                }

                "offline" ->
                {
                    if(isSocketConnected())
                        socket.emit("offline", userId)
                }

                "typing" ->
                {
                    val data = bundle.getParcelable<F61IsTypingData>(DATA)

                    val isTypingData = JSONObject()
                    isTypingData.put("chatId", data!!.chatId)
                    isTypingData.put("senderId", data.senderId)
                    isTypingData.put("status", data.status)

                    if(isSocketConnected())
                    {
                        socket.emit("typing", isTypingData)
                        Log.i(TAG, "onStartCommand: typing send")
                    }
                }

            }

        }

        return START_STICKY
    }

    private fun getSocketListener()
    {
        listenersMap[Socket.EVENT_CONNECT] = SocketEventListener(Socket.EVENT_CONNECT, this)
        listenersMap[Socket.EVENT_DISCONNECT] = SocketEventListener(Socket.EVENT_DISCONNECT, this)
        listenersMap[Socket.EVENT_CONNECT_ERROR] = SocketEventListener(Socket.EVENT_CONNECT_ERROR, this)
        listenersMap["message"] = SocketEventListener("message", this)
        listenersMap["sent"] = SocketEventListener("sent", this)
        listenersMap["delivered"] = SocketEventListener("delivered", this)
        listenersMap["viewed"] = SocketEventListener("viewed", this)
        listenersMap["updatedAt"] = SocketEventListener("updatedAt", this)
        listenersMap["new chat"] = SocketEventListener("new chat", this)
        listenersMap["online"] = SocketEventListener("online", this)
        listenersMap["offline"] = SocketEventListener("offline", this)
        listenersMap["typing"] = SocketEventListener("typing", this)
    }

    override fun onEventCall(event: String?, vararg objects: Any?)
    {

        when (event)
        {
            Socket.EVENT_CONNECT ->
            {
                joinChat()
                val msg = serviceHandler.obtainMessage()
                msg.arg1 = 1
                serviceHandler.sendMessage(msg)
            }

            Socket.EVENT_DISCONNECT ->
            {
                val msg = serviceHandler.obtainMessage()
                msg.arg1 = 2
                serviceHandler.sendMessage(msg)
            }

            Socket.EVENT_CONNECT_ERROR ->
            {
                val msg = serviceHandler.obtainMessage()
                msg.arg1 = 3
                serviceHandler.sendMessage(msg)
            }

            "sent" ->
            {
                val data = objects[0] as JSONObject

                scope.launch {
                    f61ChatRepository.updateMessageSentDateTime(data.getString("messageId"), data.getString("sentDateTime"))
                }

                rstaakUtils.saveUpdatedAt("message ${data.getString("chatId")}", data.getString("updatedAt"))

                //------------------------------------------

                intent.action = "sent"
                intent.putExtra("chatId", data.getString("chatId"))
                intent.putExtra("messageId", data.getString("messageId"))
                intent.putExtra("sentDateTime", data.getString("sentDateTime"))
                sendBroadcast(intent)
            }

            "message" ->
            {
                val data = objects[0] as JSONObject

                val messageId = data.getString("messageId")
                val chatId = data.getString("chatId")
                val senderId = data.getString("senderId")
                val message = data.getString("message")
                val sentDateTime = data.getString("sentDateTime")
                val updatedAt = data.getString("updatedAt")
                val deliveredDateTime = Date().time

                val messageDB = messageDBFactory.create(messageId, chatId, senderId, message, sentDateTime, deliveredDateTime.toString())

                scope.launch {
                    f61ChatRepository.saveMessage(messageDB)
                    rstaakUtils.saveUpdatedAt("message $chatId", updatedAt)
                }

                intent.action = "message"
                intent.putExtra("chatId", chatId)
                intent.putExtra("messageId", messageId)
                intent.putExtra("senderId", senderId)
                intent.putExtra("message", message)
                intent.putExtra("sentDateTime", sentDateTime)
                sendBroadcast(intent)

                val object1 = JSONObject()
                object1.put("chatId", chatId)
                object1.put("messageId", messageId)
                object1.put("senderId", senderId)
                object1.put("deliveredDateTime", deliveredDateTime)
                object1.put("user", userId)

                deliveredQueue.add(object1)
                if(isSocketConnected()) resendQueueDelivered()

                scope.launch {
                    f61ChatRepository.updateChatUpdatedAt(chatId, updatedAt)
                }

                intent.action = "new chat"
                sendBroadcast(intent)


                scope.launch {

                    Log.i(TAG, "onEventCall: ${F61Chat.F61ChatVisibility}  ${F6Chats.F6ChatVisibility}")

//                    if(!F61Chat.F61ChatVisibility && !F6Chats.F6ChatVisibility)
//                    {
//                        f61ChatRepository.getChat(chatId).collect {
//                            val productImage = it.productImage
//                            val productTitle = it.productTitle
//                            val ownerId = if(it.users[0] == userId) it.users[1] else it.users[0]
//                            generateNotification(this@SocketIOService, message, messageId, chatId, productTitle, productImage, ownerId)
//                        }
//                    }
                }
            }

            "delivered" ->
            {
                val data = objects[0] as JSONObject

                scope.launch {
                    f61ChatRepository.updateMessageDeliveredDataTime(data.getString("messageId"), data.getString("deliveredDateTime"))

                    Log.i(TAG, "onEventCall: deliveredDateTime saved in local")
                }

                rstaakUtils.saveUpdatedAt("message", data.getString("updatedAt"))

                intent.action = "delivered"
                intent.putExtra("chatId", data.getString("chatId"))
                intent.putExtra("messageId", data.getString("messageId"))
                intent.putExtra("deliveredDateTime", data.getString("deliveredDateTime"))
                sendBroadcast(intent)
            }

            "viewed" ->
            {
                val data = objects[0] as JSONObject

                scope.launch {
                    f61ChatRepository.updateMessageViewedDataTime(data.getString("messageId"), data.getString("viewedDateTime"))
                }

                rstaakUtils.saveUpdatedAt("message", data.getString("updatedAt"))

                intent.action = "viewed"
                intent.putExtra("chatId", data.getString("chatId"))
                intent.putExtra("messageId", data.getString("messageId"))
                intent.putExtra("viewedDateTime", data.getString("viewedDateTime"))
                sendBroadcast(intent)
            }

            "updatedAt" ->
            {
                val data = objects[0] as JSONObject
                rstaakUtils.saveUpdatedAt("message", data.getString("updatedAt"))
            }

            "new chat" ->
            {
                val data = objects[0] as JSONObject

                val object1 = data.getJSONObject("chat")

                var productImage = ""
                if(data.getString("imageList") != "")
                {
                    val array = JSONArray(data.getString("imageList"))
                    productImage = array.get(0).toString()
                }


                val users = Gson().fromJson(object1.getJSONArray("users").toString(), Array<String>::class.java).toList()
                val chatDB = chatDBFactory.create(object1.getString("_id"), object1.getString("productId"), users , data.getString("productTitle"), productImage, "", object1.getString("createdAt"), object1.getString("updatedAt"))

                scope.launch {
                    f61ChatRepository.saveChat(chatDB)
                }

                intent.action = "new chat"
                sendBroadcast(intent)

                Log.i(TAG, "onEventCall: new chat save in local")
            }

            "online" ->
            {
                val data = objects[0] as JSONObject

                intent.action = "online"
                intent.putExtra("userId", data.getString("userId"))
                sendBroadcast(intent)
            }

            "offline" ->
            {
                val data = objects[0] as JSONObject

                intent.action = "offline"
                intent.putExtra("userId", data.getString("userId"))
                sendBroadcast(intent)
            }

            "typing" ->
            {
                val data = objects[0] as JSONObject

                intent.action = "typing"
                intent.putExtra("data", data.toString())
                sendBroadcast(intent)
            }

        }
    }

    private fun isSocketConnected(): Boolean
    {
        if(!socket.connected())
        {
            socket.connect()
            Log.i(TAG, "reconnecting socket...")
            return false
        }
        return true
    }

    private fun joinChat()
    {
        val data = JSONObject()
        data.put("userId", userId)

        socket.emit("join", data)

        resendQueueMessages()
        resendQueueDelivered()
        resendQueueViewed()

        chatAndMessageList()
    }

    private fun chatAndMessageList()
    {
        scope.launch {
            f61ChatRepository.chatAndMessageList().collect {
                when (it.status)
                {
                    MyResult.Status.SUCCESS ->
                    {
                        val result = it.data

                        if(result!!.status == 200)
                        {
                            val chatData = result.message.chatData
                            val messageData = result.message.messageData

                            for (item in chatData)
                            {
                                val chat = item.chat

                                var image = ""

                                val array = JSONArray(item.imageList)
                                if(array.length() > 0) image = array.get(0).toString()

                                val chatDB = chatDBFactory.create(chat.chatId, chat.productId, chat.users, item.productTitle, image, "", chat.createdAt.toString(), chat.updatedAt.toString())

                                f61ChatRepository.saveChat(chatDB)

                                intent.action = "new chat"
                                sendBroadcast(intent)

                                Log.i(TAG, "joinChat: chat ${chat.chatId} saved in local")
                            }

                            for (item in messageData)
                            {
                                if(item.deliveredDateTime == "0")
                                {
                                    val deliveredDateTime = Date(). time
                                    item.deliveredDateTime = deliveredDateTime.toString()
                                    val object1 = JSONObject()
                                    object1.put("messageId", item.messageId)
                                    object1.put("chatId", item.chatId)
                                    object1.put("senderId", item.senderId)
                                    object1.put("deliveredDateTime", deliveredDateTime)
                                    object1.put("user", userId)

                                    if(isSocketConnected())
                                    {
                                        socket.emit("delivered-message", object1)
                                        Log.i(TAG, "joinChat: delivered-message sent to server")
                                    }
                                    else
                                    {
                                        deliveredQueue.add(object1)
                                        Log.i(TAG, "joinChat: delivered-message push to queue")
                                    }
                                }

                                f61ChatRepository.saveMessage(item)
                                Log.i(TAG, "joinChat: message ${item.messageId} saved in local")

                                rstaakUtils.saveUpdatedAt("message", item.updatedAt.toString())
                            }
                        }
                    }
                    MyResult.Status.ERROR -> Log.i(TAG, "joinChat: ${it.error}")
                    MyResult.Status.LOADING -> Log.i(TAG, "joinChat: ${it.status}")
                }
            }
        }
    }

    companion object
    {
        private const val TAG = "SocketIOService"
        const val EVENT_TYPE = "eventType"
        const val DATA = "data"
    }

    private fun resendQueueMessages()
    {
        val message = messageQueue.poll()
        if(message != null)
        {
            socket.emit("send-message", message)
            resendQueueMessages()
        }
    }

    private fun resendQueueDelivered()
    {
        val deliveredDateTime = deliveredQueue.poll()
        if(deliveredDateTime != null)
        {
            socket.emit("delivered-message", deliveredDateTime)
            resendQueueDelivered()
        }
    }

    private fun resendQueueViewed()
    {
        val viewedDateTime = viewedQueue.poll()
        if(viewedDateTime != null)
        {
            socket.emit("viewed-message", viewedDateTime)
            resendQueueViewed()
        }
    }

//    private fun generateNotification(context: Context, message: String?, messageId: String?, chatId: String?, productTitle: String?, productImage: String?, ownerId: String?)
//    {
//        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = ""
//
//        val intent = Intent(context, F61Chat::class.java)
//        intent.putExtra("chatId", chatId)
//        intent.putExtra("productTitle", productTitle)
//        intent.putExtra("productImage", productImage)
//        intent.putExtra("ownerId", ownerId)
//
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "")
//            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.image))
//            .setSmallIcon(R.drawable.image).setContentTitle(productTitle).setContentText(message)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            val channelName = "Message Notification"
//            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
//            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//        notificationManager.notify(messageId, 0, builder.build())
//    }

    override fun onDestroy()
    {
        super.onDestroy()

        Log.i(TAG, "onDestroy: stop service")

        //-------------------------------------------

        socket.disconnect()
        messageQueue.clear()
        for ((key, value) in listenersMap)
        {
            socket.off(key, value)
        }

    }
}