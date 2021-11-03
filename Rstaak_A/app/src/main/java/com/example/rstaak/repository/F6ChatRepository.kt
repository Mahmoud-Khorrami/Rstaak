package com.example.rstaak.repository

import com.example.rstaak.database.MyDAO
import javax.inject.Inject

class F6ChatRepository @Inject constructor(myDAO: MyDAO)
{
    var chats = myDAO.getAllChats()
}