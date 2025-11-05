package com.example.superassistant

import com.google.gson.JsonObject

internal class ChatRepository(private val retrofit: SuperAssistantRetrofit) {

    suspend fun sendRequest(messages: List<Message>) : Result<JsonObject> {
        val modelUri = "gpt://${Keys.ID}/yandexgpt-lite"

        val api = retrofit.createApi(Keys.SECURE_KEY)

        val prompt = Prompt(
            modelUri = modelUri,
            completionOptions = CompletionOptions(stream = false, temperature = 0.6, maxTokens = "2000"),
            messages = messages
        )

        return try {
            val response = api.get(prompt)
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
