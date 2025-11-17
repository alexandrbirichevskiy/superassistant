package com.example.superassistant.chatgpt

import android.util.Log
import com.example.superassistant.Keys
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.chatgpt.dto.ChatGptModelsDTO
import com.example.superassistant.chatgpt.dto.ChatGptRequestDTO
import com.example.superassistant.chatgpt.models.ChatGptModel

internal class ChatGptRepository(private val retrofit: SuperAssistantRetrofit) {

    val api by lazy {
        retrofit.createApi(
            "Bearer ${Keys.CHAT_GPT_KEY}",
            BASE_URL,
            ChatGptApi::class.java
        )
    }

    suspend fun getModels(): List<ChatGptModel> {
        val response = api.getModels()
        return response.body()?.toChatGptModel().orEmpty()
    }

    suspend fun sendMessage(model: String, input: String) {
        val response = api.get(
            ChatGptRequestDTO(
                model = model,
                input = input
            )
        )

        Log.e("OLOLO", "${response.body()}")
    }

    private fun ChatGptModelsDTO.toChatGptModel() = data.map {
        ChatGptModel(
            id = it.id,
            type = it.type,
            created = it.created,
            ownedBy = it.ownedBy,
        )
    }

    private companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }
}