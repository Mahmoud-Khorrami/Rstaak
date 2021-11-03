package com.example.rstaak.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Entity(tableName = "chat_db_table")
class ChatDB @AssistedInject constructor(

    @PrimaryKey @Assisted("chatId") var chatId: String,
    @Assisted("productId") var productId: String,
    @Assisted("users") var users: List<String>,
    @Assisted("productTitle") var productTitle: String,
    @Assisted("productImage") var productImage: String,
    @Assisted("userName") var userName: String,
    @Assisted("createdAt") var createdAt: String,
    @Assisted("updatedAt") var updatedAt: String
)
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("chatId") chatId: String,
            @Assisted("productId") productId: String,
            @Assisted("users") users: List<String>,
            @Assisted("productTitle") productTitle: String,
            @Assisted("productImage") productImage: String,
            @Assisted("userName") userName: String,
            @Assisted("createdAt") createdAt: String,
            @Assisted("updatedAt") updatedAt: String): ChatDB
    }
}