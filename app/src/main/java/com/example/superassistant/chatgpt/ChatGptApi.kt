package com.example.superassistant.chatgpt

import com.example.superassistant.chatgpt.dto.ChatGptModelsDTO
import com.example.superassistant.chatgpt.dto.ChatGptRequestDto
import com.example.superassistant.chatgpt.dto.ChatGptResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatGptApi {
    @GET("models")
    suspend fun getModels(): Response<ChatGptModelsDTO>

    @POST("chat/completions")
    suspend fun get(@Body request: ChatGptRequestDto): Response<ChatGptResponseDto>
}