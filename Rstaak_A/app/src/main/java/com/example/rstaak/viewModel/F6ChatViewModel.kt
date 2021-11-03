package com.example.rstaak.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rstaak.database.ChatsWithMessages
import com.example.rstaak.repository.F6ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class F6ChatViewModel @Inject constructor(var f6ChatRepository: F6ChatRepository): ViewModel()
{
    var chatList = MutableLiveData<List<ChatsWithMessages>>()

    init
    {
        getChats()
    }

    fun getChats()
    {
        viewModelScope.launch {
            f6ChatRepository.chats.collect {
                chatList.value = it
            }
        }
    }
}