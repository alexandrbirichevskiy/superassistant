package com.example.superassistant.huggingface

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HuggingFaceApi {
    @POST("chat/completions")
    suspend fun get(@Body prompt: ChatRequestDTO): Response<JsonObject>
}