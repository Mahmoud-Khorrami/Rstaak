package com.example.rstaak.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.rstaak.database.ChatDB
import com.example.rstaak.database.MessageDB
import com.example.rstaak.database.MyDAO
import com.example.rstaak.general.MyResponse
import com.example.rstaak.general.MyResult
import com.example.rstaak.general.RstaakUtils
import com.example.rstaak.req_res.*
import com.example.rstaak.retrofit.ChatApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class F61ChatRepository @Inject constructor(
    var chatApiInterface: ChatApiInterface,
    var executor: ExecutorService,
    var myDAO: MyDAO,
    var sharedPreferences: SharedPreferences): MyResponse()
{
    @Inject
    lateinit var chatDBFactory: ChatDB.Factory
    @Inject
    lateinit var rstaakUtils: RstaakUtils
    @Inject
    lateinit var f61CheckOnlineRequestFactory: F61CheckOnlineRequest.Factory
    @Inject
    lateinit var f61ChatAndMessageListRequestFactory: F61ChatAndMessageListRequest.Factory
    private var userId = sharedPreferences.getString("userId", null).toString()

    suspend fun createChat(f61CreateChatRequest: F61CreateChatRequest): Flow<MyResult<F61CreateChatResponse>>
    {
        return flow {

            emit(MyResult.loading())
            val result = getResult { chatApiInterface.createChat(f61CreateChatRequest) }
            emit(result)

            if(result.status == MyResult.Status.SUCCESS)
            {
                val message = result.data?.message
                val chat = message?.chat
                val chatDB = chatDBFactory.create(chat!!.id, chat.productId, chat.users, message.productTitle, message.imageList, "", chat.createdAt.toString(), chat.updatedAt.toString())

                saveChat(chatDB)
            }

        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveChat(chatDb: ChatDB)
    {
        myDAO.saveChat(chatDb)

        rstaakUtils.saveUpdatedAt("chat", chatDb.updatedAt)
    }

    suspend fun saveMessage(messageDB: MessageDB)
    {
        myDAO.saveMessage(messageDB)
    }

    suspend fun updateMessageSentDateTime(messageId: String, sentDateTime: String)
    {
        myDAO.updateMessageSentDateTime(messageId, sentDateTime)
    }

    suspend fun updateChatUpdatedAt(chatId: String, updatedAt: String)
    {
        myDAO.updateChatUpdatedAt(chatId, updatedAt)
    }

    suspend fun checkOnline(userId: String): Flow<MyResult<F61CheckOnlineResponse>>
    {
        val request = f61CheckOnlineRequestFactory.create(userId)

        return flow {
            emit(getResult {  chatApiInterface.checkOnline(request) })
        }

    }

    fun getMessages(chatId: String) = myDAO.getMessages(chatId)

    suspend fun updateMessageViewedDataTime(messageId: String, viewedDateTime: String)
    {
        myDAO.updateMessageViewedDateTime(messageId, viewedDateTime)
    }

    suspend fun updateMessageDeliveredDataTime(messageId: String, deliveredDateTime: String)
    {
        myDAO.updateMessageDeliveredDateTime(messageId, deliveredDateTime)
    }

    suspend fun chatAndMessageList(): Flow<MyResult<F61ChatAndMessageListResponse>>
    {
        return flow {
            val chatUpdatedAt = if(rstaakUtils.getUpdatedAt("chat") != null) rstaakUtils.getUpdatedAt("chat") else ""
            val messageUpdatedAt = if(rstaakUtils.getUpdatedAt("message") != null) rstaakUtils.getUpdatedAt("message") else ""

            val f61ChatAndMessageListRequest = f61ChatAndMessageListRequestFactory.create(userId, chatUpdatedAt, messageUpdatedAt)

            val result = getResult { chatApiInterface.chatAndMessageList(f61ChatAndMessageListRequest) }
            emit(result)

        }.flowOn(Dispatchers.IO)
    }

//    fun getChat(chatId: String) = myDAO.getChat(chatId)

    companion object
    {
        private const val TAG = "F61ChatRepository"
    }
}