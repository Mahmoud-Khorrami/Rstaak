package com.example.rstaak.viewModel

import android.content.SharedPreferences
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rstaak.adapter.F61Adapter
import com.example.rstaak.database.MessageDB
import com.example.rstaak.general.MyResult
import com.example.rstaak.model.f3_product.F3ChatStatus
import com.example.rstaak.model.f61_chat.F61ParentModel
import com.example.rstaak.repository.F61ChatRepository
import com.example.rstaak.req_res.F61CreateChatRequest
import com.example.rstaak.req_res.F61CreateChatResponse
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class F61ChatViewModel @Inject constructor(
    var f61ChatRepository: F61ChatRepository,
    val savedStateHandle: SavedStateHandle,
    var sharedPreferences: SharedPreferences):ViewModel()
{
    var f61CreateChatResponseData = MutableLiveData<MyResult<F61CreateChatResponse>>()
    var online = MutableLiveData<String>()
    var messages = MutableLiveData<List<MessageDB>>(emptyList())
    private lateinit var chatId: String
    private lateinit var ownerId : String
    lateinit var productTitle: MutableLiveData<String>

    init
    {
        val args: F3ChatStatus? = savedStateHandle["chatStatus"]

        if(args != null)
        {
            chatId = args.chatId.toString()
            ownerId = args.ownerId
            productTitle.value = args.productTitle

        }

        checkOnline(ownerId)

        if(chatId.isNotEmpty())
            getMessages(chatId)
    }

    fun createChat(f61CreateChatRequest: F61CreateChatRequest)
    {
        viewModelScope.launch {
            f61ChatRepository.createChat(f61CreateChatRequest).collect {
                f61CreateChatResponseData.value = it
            }
        }
    }

    fun checkOnline(ownerId: String)
    {
        viewModelScope.launch {
            f61ChatRepository.checkOnline(ownerId).collect {

                if(it.data?.status == 200)
                    online.value = "online"
                else
                    online.value = ""
            }
        }
    }

    private fun getMessages(chatId: String)
    {
        viewModelScope.launch {
            f61ChatRepository.getMessages(chatId).collect {
                messages.value = it
            }
        }
    }

}