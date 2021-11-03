package com.example.rstaak.database

import android.content.Context
import androidx.room.*
import com.example.rstaak.general.ListConverter


@Database(entities = [ChatDB::class, MessageDB::class], version = 2)
@TypeConverters(ListConverter::class)
abstract class MyDataBase : RoomDatabase()
{
    abstract val myDAO: MyDAO

    companion object
    {
        private var INSTANCE: MyDataBase? = null

        fun getInstance(context: Context): MyDataBase
        {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null)
                {
                    instance = Room.databaseBuilder(context.applicationContext, MyDataBase::class.java, "chat_data_database")
                        .build()
                }
                return instance
            }
        }
    }
}