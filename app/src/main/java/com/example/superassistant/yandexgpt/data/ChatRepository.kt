package com.example.superassistant.yandexgpt.data

import CompletionOptionsDBO
import MessageDBO
import RequestDBO
import ResponseFormatDBO
import com.example.superassistant.Keys
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.yandexgpt.data.database.ChatDao
import com.example.superassistant.yandexgpt.data.database.DialogsDao
import com.example.superassistant.yandexgpt.data.network.dto.CompletionOptionsRequestDTO
import com.example.superassistant.yandexgpt.data.network.dto.PromptRequestDTO
import com.example.superassistant.yandexgpt.data.network.dto.TypeRequestDTO
import com.example.superassistant.yandexgpt.data.network.YandexGptApi
import com.example.superassistant.yandexgpt.data.network.dto.MessageRequestDTO
import com.example.superassistant.yandexgpt.data.network.dto.ResultResponseDTO
import com.example.superassistant.yandexgpt.data.network.dto.RootResponseDTO
import com.example.superassistant.yandexgpt.presentation.Dialog
import com.example.superassistant.yandexgpt.presentation.models.Agent
import com.google.gson.JsonObject

class ChatRepository(
    private val retrofit: SuperAssistantRetrofit,
    private val dao: ChatDao
) {

    val api by lazy {
        retrofit.createApi(
            "Api-Key ${Keys.SECURE_KEY}",
            BASE_URL,
            YandexGptApi::class.java
        )
    }

    private var lastPrompt: PromptRequestDTO? = null

    suspend fun saveRequest(dialog: Dialog) {
        lastPrompt?.let { prompt ->
            dao.insertRequest(
                RequestDBO(
                    id = dialog.id.toLong(),
                    completionOptions = CompletionOptionsDBO(
                        maxTokens = prompt.completionOptions.maxTokens,
                        stream = prompt.completionOptions.stream,
                        temperature = prompt.completionOptions.temperature,
                        responseFormat = ResponseFormatDBO(
                            type = prompt.completionOptions.responseFormat.type
                        )
                    ),
                    modelUri = prompt.modelUri ,
                    messages = prompt.messages.map {
                        MessageDBO(role = it.role, text = it.text)
                    }
                )
            )
        }
    }

    suspend fun getChat(id: Long) = dao.getRequestById(id)


    suspend fun sendRequest(
        agent: Agent,
    ): Result<RootResponseDTO> {

        val prompt = PromptRequestDTO(
            modelUri = "gpt://${Keys.ID}/${agent.name}",
            completionOptions = CompletionOptionsRequestDTO(
                stream = false,
                temperature = agent.temperature,
                maxTokens = agent.maxTokens,
                responseFormat = TypeRequestDTO("json")
            ),
            messages = agent.history
        )

        return try {
            val response = api.get(prompt)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    lastPrompt = prompt.copy(
                        messages = prompt.messages + MessageRequestDTO(
                            role = body.result.alternatives.first().message.role,
                            text = body.result.alternatives.first().message.text
                        )
                    )
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