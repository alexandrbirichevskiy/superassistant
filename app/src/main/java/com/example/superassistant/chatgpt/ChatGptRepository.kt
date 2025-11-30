package com.example.superassistant.chatgpt

import CompletionOptionsDBO
import MessageDBO
import RequestDBO
import ResponseFormatDBO
import com.example.superassistant.Keys
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.chatgpt.dto.ChatGptMessageDto
import com.example.superassistant.chatgpt.dto.ChatGptRequestDto
import com.example.superassistant.chatgpt.dto.ChatGptResponseDto
import com.example.superassistant.chatgpt.dto.Tool
import com.example.superassistant.chatgpt.dto.ToolFunction
import com.example.superassistant.yandexgpt.data.database.ChatDao
import com.example.superassistant.yandexgpt.presentation.Dialog
import com.example.superassistant.yandexgpt.presentation.models.Agent
import kotlin.collections.plus

class ChatGptRepository(
    private val retrofit: SuperAssistantRetrofit,
    private val dao: ChatDao
) {

    val api by lazy {
        retrofit.createApi(
            "Bearer ${Keys.CHAT_GPT_KEY}",
            BASE_URL,
            ChatGptApi::class.java
        )
    }

    private var lastPrompt: ChatGptRequestDto? = null

    suspend fun saveRequest(dialog: Dialog) {
        lastPrompt?.let { prompt ->
            dao.insertRequest(
                RequestDBO(
                    id = dialog.id.toLong(),
                    completionOptions = CompletionOptionsDBO(
                        maxTokens = "123",
                        stream = false,
                        temperature = prompt.temperature ?: 0.0,
                        responseFormat = ResponseFormatDBO(
                            type = "prompt.completionOptions.responseFormat.type"
                        )
                    ),
                    modelUri = prompt.model,
                    messages = prompt.messages.map {
                        MessageDBO(role = it.role, text = it.content)
                    }
                )
            )
        }
    }

    suspend fun getChat(id: Long) = dao.getRequestById(id)

    suspend fun getModels(): List<String> {
        val response = api.getModels()
        return response.body()?.data?.map { it.id } ?: emptyList<String>()
    }

    suspend fun sendRequest(
        agent: Agent,
    ): Result<ChatGptResponseDto> {

        val tools = listOf(
            Tool(
                function = ToolFunction(
                    name = "add_movie",
                    description = "Добавить фильм в список к просмотру. " +
                            "определяешь жанр, год и подробный description сам (если они не указаны)",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "title" to mapOf("type" to "string"),
                            "genre" to mapOf("type" to "string"),
                            "year" to mapOf("type" to "integer"),
                            "description" to mapOf("type" to "string")
                        ),
                        "required" to listOf("title")
                    )
                )
            ),
            Tool(
                function = ToolFunction(
                    name = "get_movie_short",
                    description = "Найти в интернете и рассказать краткое описание о фильме. " +
                            "определяешь описание cам (если они не указаны) и выводишь его в content",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "title" to mapOf("type" to "string"),
                        ),
                        "required" to listOf("title")
                    )
                )
            ),
            Tool(
                function = ToolFunction(
                    name = "save_movie_description",
                    description = "Сохраняет описание фильма",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "title" to mapOf("type" to "string"),
                            "description" to mapOf("type" to "string")
                        ),
                        "required" to listOf("title")
                    )
                )
            ),
            Tool(
                function = ToolFunction(
                    name = "get_last_three_movies",
                    description = "Показать последние добавленные фильма",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "title" to mapOf("type" to "string"),
                        ),
                    )
                )
            )
        )


        val prompt = ChatGptRequestDto(
            model = agent.name,
            temperature = agent.temperature,
//            maxTokens = agent.maxTokens.toInt(),
            stream = false,
            messages = agent.history.map { ChatGptMessageDto(it.role, it.text) },
//            tools = tools
        )

        return try {
            val response = api.get(prompt)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    lastPrompt = prompt.copy(
                        messages = prompt.messages + ChatGptMessageDto(
                            role = prompt.messages.first().role,
                            prompt.messages.first().content
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
        const val BASE_URL = "https://api.openai.com/v1/"
    }
}