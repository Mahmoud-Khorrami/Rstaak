package com.example.rstaak.req_res

import android.os.Parcelable
import com.example.rstaak.database.MessageDB
import com.google.gson.annotations.SerializedName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

data class F61CreateChatRequest @AssistedInject constructor(
    @Assisted("senderId") var senderId: String,
    @Assisted("receiverId") var receiverId: String,
    @Assisted("productId") var productId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("senderId") senderId: String,
            @Assisted("receiverId") receiverId: String,
            @Assisted("productId") productId: String): F61CreateChatRequest
    }
}

data class F61CreateChatResponse (var status: Int, var message: Message1)
data class Message1(var chat: Chat, var productTitle: String, var imageList: String)
data class Chat(@SerializedName("_id") var id:String, var productId: String, var users: List<String>, var createdAt: Long, var updatedAt: Long)

@Parcelize
data class F61Message @AssistedInject constructor(
    @Assisted("messageId") var messageId: String,
    @Assisted("chatId") var chatId: String,
    @Assisted("message") var message: String): Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("messageId") messageId: String,
            @Assisted("chatId") chatId: String,
            @Assisted("message") message: String):F61Message
    }
}

data class F61CheckOnlineRequest @AssistedInject constructor(@Assisted var userId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(userId: String): F61CheckOnlineRequest
    }
}

data class F61CheckOnlineResponse(var status: Int)

@Parcelize
data class F61MessageViewed @AssistedInject constructor(
    @Assisted("messageId") var messageId: String,
    @Assisted("chatId") var chatId: String,
    @Assisted("senderId") var senderId: String,
    @Assisted("viewedDateTime") var viewedDateTime: String): Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("messageId") messageId: String,
            @Assisted("chatId") chatId: String,
            @Assisted("senderId") senderId: String,
            @Assisted("viewedDateTime") viewedDateTime: String): F61MessageViewed
    }
}

data class F61ChatAndMessageListRequest @AssistedInject constructor(
    @Assisted("userId") var userId: String,
    @Assisted("chatUpdatedAt") var chatUpdatedAt: String?,
    @Assisted("messageUpdatedAt") var messageUpdatedAt: String?
)
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("userId") userId: String,
            @Assisted("chatUpdatedAt") chatUpdatedAt: String?,
            @Assisted("messageUpdatedAt") messageUpdatedAt: String?): F61ChatAndMessageListRequest
    }
}

data class F61ChatAndMessageListResponse (var status: Int, var message: Message2)
data class Message2(var messageData: List<MessageDB>, var chatData: List<Chat2>)
data class Chat1(var users: List<String>, @SerializedName("_id") var chatId: String, var productId: String, var createdAt: Long, var updatedAt: Long)
data class Chat2(var chat: Chat1, var productTitle: String, var imageList: String)

@Parcelize
data class F61IsTypingData @AssistedInject constructor(
    @Assisted("chatId") var chatId: String,
    @Assisted("senderId") var senderId: String,
    @Assisted("status") var status: String): Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("chatId") chatId: String,
            @Assisted("senderId") senderId: String,
            @Assisted("status") status: String): F61IsTypingData
    }
}