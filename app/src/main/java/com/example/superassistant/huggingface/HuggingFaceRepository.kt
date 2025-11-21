package com.example.superassistant.huggingface

import android.util.Log
import com.example.superassistant.Keys
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.yandexgpt.data.network.dto.MessageRequestDTO
import com.example.superassistant.yandexgpt.presentation.models.Agent
import com.google.gson.JsonObject

internal class HuggingFaceRepository(private val retrofit: SuperAssistantRetrofit) {

    val api by lazy {
        retrofit.createApi(
            "Bearer ${Keys.HUGGING_FACE_TOKEN}",
            BASE_URL,
            HuggingFaceApi::class.java
        )
    }

    suspend fun sendRequest(
        useProModel: Boolean,
        agent: Agent
    ): Result<JsonObject> {

        val request = ChatRequestDTO(
            messages = agent.history.map { it.toMessageDTO() },
            model = agent.name
        )

        return try {
            val response = api.get(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.w("OLOLO", body.toString())
                    body.addProperty("time", response.headers()["Time"])
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val err = response.errorBody()?.string()
                Result.failure(Exception("HTTP ${response.code()} $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun MessageRequestDTO.toMessageDTO() = MessageDTO(
        role = role,
        content = text
    )


    private companion object {
        const val BASE_URL = "https://router.huggingface.co/v1/"
    }
}