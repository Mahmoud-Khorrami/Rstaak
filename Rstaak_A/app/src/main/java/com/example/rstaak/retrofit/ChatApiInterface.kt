package com.example.rstaak.retrofit

import com.example.rstaak.req_res.*
import com.example.rstaak.req_res.f3_product.F3Request3
import com.example.rstaak.req_res.f3_product.F3Response3
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiInterface
{

    @POST("/api/chat-status")
    suspend fun chatStatus(@Body f3Request3: F3Request3): Response<F3Response3>

    @POST("/api/create-chat")
    suspend fun createChat(@Body f61CreateChatRequest: F61CreateChatRequest): Response<F61CreateChatResponse>

    @POST("/online")
    suspend fun checkOnline(@Body f61CheckOnlineRequest: F61CheckOnlineRequest): Response<F61CheckOnlineResponse>

    @POST("/api/chat-and-message-list")
    suspend fun chatAndMessageList(@Body f61ChatAndMessageListRequest: F61ChatAndMessageListRequest): Response<F61ChatAndMessageListResponse>
}