package com.example.rstaak.database

import androidx.room.Embedded
import androidx.room.Relation

data class ChatsWithMessages(
    @Embedded var chatDB: ChatDB,
    @Relation(
        parentColumn = "chatId",
        entityColumn = "chatId"
    )
    val messages : List<MessageDB>
)
