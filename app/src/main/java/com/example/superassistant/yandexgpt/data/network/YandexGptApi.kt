package com.example.superassistant.yandexgpt.data.network

import com.example.superassistant.yandexgpt.data.network.dto.PromptRequestDTO
import com.example.superassistant.yandexgpt.data.network.dto.RootResponseDTO
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface YandexGptApi {
    @POST("foundationModels/v1/completion")
    suspend fun get(@Body prompt: PromptRequestDTO): Response<RootResponseDTO>
}