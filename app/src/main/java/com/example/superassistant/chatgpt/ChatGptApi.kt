package com.example.superassistant.chatgpt

import com.example.superassistant.chatgpt.dto.ChatGptRequestDTO
import com.example.superassistant.chatgpt.dto.ChatGptModelsDTO
import com.example.superassistant.chatgpt.dto.ChatGptResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatGptApi {
    @GET("models")
    suspend fun getModels(): Response<ChatGptModelsDTO>

    @POST("responses")
    suspend fun get(@Body request: ChatGptRequestDTO): Response<ChatGptResponseDTO>
}