package com.example.rstaak.model.f61_chat

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

open class F61ParentModel(var currentType: Int)
{
    companion object
    {
        const val SendMessage = 100
        const val ReceiveMessage = 200
        const val Loading = 300
    }
}

class F61Loading @Inject constructor(): F61ParentModel(Loading)

class F61ReceiveMessageModel @AssistedInject constructor(
    @Assisted("messageId") var messageId: String,
    @Assisted("senderId") var senderId: String,
    @Assisted("message") var message: String,
    @Assisted("sentDateTime") var sentDateTime: String,
    @Assisted("deliveredDateTime") var deliveredDateTime: String,
    @Assisted("viewedDateTime") var viewedDateTime: String): F61ParentModel(ReceiveMessage)
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("messageId") messageId: String,
            @Assisted("senderId") senderId: String,
            @Assisted("message") message: String,
            @Assisted("sentDateTime") sentDateTime: String,
            @Assisted("deliveredDateTime") deliveredDateTime: String,
            @Assisted("viewedDateTime") viewedDateTime: String): F61ReceiveMessageModel
    }
}

class F61SendMessageModel @AssistedInject constructor(
    @Assisted("messageId") var messageId: String,
    @Assisted("senderId") var senderId: String,
    @Assisted("message") var message: String,
    @Assisted("sentDateTime") var sentDateTime: String,
    @Assisted("deliveredDateTime") var deliveredDateTime: String,
    @Assisted("viewedDateTime") var viewedDateTime: String): F61ParentModel(SendMessage)
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("messageId") messageId: String,
            @Assisted("senderId") senderId: String,
            @Assisted("message") message: String,
            @Assisted("sentDateTime") sentDateTime: String,
            @Assisted("deliveredDateTime") deliveredDateTime: String,
            @Assisted("viewedDateTime") viewedDateTime: String): F61SendMessageModel
    }
}