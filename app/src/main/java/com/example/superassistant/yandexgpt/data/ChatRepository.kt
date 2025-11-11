package com.example.superassistant.yandexgpt.data

import android.util.Log
import com.example.superassistant.Keys
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.yandexgpt.presentation.Agent
import com.google.gson.JsonObject
import kotlin.getValue

internal class ChatRepository(private val retrofit: SuperAssistantRetrofit) {

    val api by lazy {
        retrofit.createApi(
            "Api-Key ${Keys.SECURE_KEY}",
            BASE_URL,
            LlmApi::class.java
        )
    }

    suspend fun sendRequest(
        useProModel: Boolean,
        agent: Agent
    ) : Result<JsonObject> {


        val model = if (useProModel) "yandexgpt/latest" else "yandexgpt-lite"

        val prompt = Prompt(
            modelUri = "gpt://${Keys.ID}/$model",
            completionOptions = CompletionOptions(
                stream = false,
                temperature = agent.temperature,
                maxTokens = "2000",
                responseFormat = Type("json")
            ),
            messages = agent.history
        )

        return try {
            val response = api.get(prompt)
            Log.i("OLOLO", model)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
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

    private companion object {
        const val BASE_URL = "https://llm.api.cloud.yandex.net/"
    }
}