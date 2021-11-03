package com.example.rstaak.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Entity(tableName = "message_db_table")
data class MessageDB @AssistedInject constructor(
    @PrimaryKey @Assisted("messageId") var messageId: String,
    @Assisted("chatId") var chatId: String,
    @Assisted("senderId") var senderId: String,
    @Assisted("message") var message: String,
    @Assisted("sentDateTime") var sentDateTime: String? = "0",
    @Assisted("deliveredDateTime") var deliveredDateTime: String? = "0",
    @Assisted("viewedDateTime") var viewedDateTime: String? = "0",
    @Assisted("updatedAt") var updatedAt: String? = "")
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("messageId") messageId: String,
            @Assisted("chatId") chatId: String,
            @Assisted("senderId") senderId: String,
            @Assisted("message") message: String,
            @Assisted("sentDateTime") sentDateTime: String? = "0",
            @Assisted("deliveredDateTime") deliveredDateTime: String? = "0",
            @Assisted("viewedDateTime") viewedDateTime: String? = "0",
            @Assisted("updatedAt") updatedAt: String? = ""): MessageDB
    }
}
