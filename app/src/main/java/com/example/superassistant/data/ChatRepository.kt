package com.example.superassistant.data

import android.util.Log
import com.example.superassistant.Keys
import com.example.superassistant.presentation.Agent
import com.google.gson.JsonObject

internal class ChatRepository(private val retrofit: SuperAssistantRetrofit) {

    suspend fun sendRequest(
        useProModel: Boolean,
        agent: Agent
    ) : Result<JsonObject> {

        val model = if (useProModel) "yandexgpt/latest" else "yandexgpt-lite"

        val api = retrofit.createApi(Keys.SECURE_KEY)

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
}