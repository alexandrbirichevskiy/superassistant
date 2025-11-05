package com.example.superassistant

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LlmApi {
    @POST("foundationModels/v1/completion")
    suspend fun get(@Body prompt: Prompt): Response<JsonObject>
}