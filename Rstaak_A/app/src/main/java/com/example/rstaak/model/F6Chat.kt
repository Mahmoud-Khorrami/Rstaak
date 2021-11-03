package com.example.rstaak.model

import com.example.rstaak.database.ChatDB
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

open class F6ParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 101
        const val NotFound = 401
    }
}

data class F6ChatModel @AssistedInject constructor(
    @Assisted var chatDB: ChatDB,
    @Assisted var unreadMessage: Int): F6ParentModel(Main)
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            chatDB: ChatDB,
            unreadMessage: Int): F6ChatModel
    }
}

class F5NotFound @Inject constructor(): F6ParentModel(NotFound)