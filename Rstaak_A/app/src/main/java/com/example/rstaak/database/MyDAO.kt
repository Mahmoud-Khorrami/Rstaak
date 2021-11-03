package com.example.rstaak.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDAO
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChat(chatDb: ChatDB): Long

    @Query("SELECT * FROM chat_db_table order by updatedAt DESC")
    fun getAllChats(): Flow<List<ChatsWithMessages>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessage(messageDB: MessageDB): Long

    @Query("UPDATE message_db_table SET sentDateTime = :sentDateTime WHERE messageId = :messageId")
    suspend fun updateMessageSentDateTime(messageId: String , sentDateTime: String)

    @Query("UPDATE chat_db_table SET updatedAt = :updatedAt WHERE chatId = :chatId")
    suspend fun updateChatUpdatedAt(chatId: String, updatedAt: String)

    @Query("SELECT * FROM message_db_table WHERE chatId = :chatId")
    fun getMessages(chatId: String): Flow<List<MessageDB>>

    @Query("UPDATE message_db_table SET viewedDateTime = :viewedDateTime WHERE messageId = :messageId")
    suspend fun updateMessageViewedDateTime(messageId: String, viewedDateTime: String)

    @Query("UPDATE message_db_table SET deliveredDateTime = :deliveredDateTime WHERE messageId = :messageId")
    suspend fun updateMessageDeliveredDateTime(messageId: String, deliveredDateTime: String)

    @Query("SELECT * FROM chat_db_table WHERE chatId = :chatId")
    fun getChat(chatId: String): Flow<ChatDB>
}