package com.example.superassistant.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superassistant.data.ChatRepository
import com.example.superassistant.data.Message
import com.example.superassistant.data.SuperAssistantRetrofit
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel() : ViewModel() {

    val messages = mutableStateListOf<ChatMessageUi>()
    var isLoading = mutableStateOf(false)
    var lastError = mutableStateOf<String?>(null)

    val agentMath = Agent(
        system = "Ты консультант, коротко рассказывающий 3 факта о животном, введенном пользователем",
        name = "Консультант с температурой 0.0",
        temperature = 0.0
    )

    val agentPhil = Agent(
        system = "Ты консультант, коротко рассказывающий 3 факта о животном, введенном пользователем",
        name = "Консультант с температурой 0.5",
        temperature = 0.5
    )

    val agentMusic = Agent(
        system = "Ты консультант, коротко рассказывающий 3 факта о животном, введенном пользователем",
        name = "Консультант с температурой 1.0",
        temperature = 1.0
    )

    val expertAgent = Agent(
        system = "Под какие задачи подходят агенты с температурой 1; 0.5; 0",
        name = "Эксперт",
        temperature = 0.5
    )

    val agentList = listOf<Agent>(agentMusic, agentPhil, agentMath)
    val agentProList = listOf(expertAgent)
    val usingProModel = MutableStateFlow(false)
    private val retrofit = SuperAssistantRetrofit()
    private val repository = ChatRepository(retrofit)

    fun sendUserMessage(isSystem: Boolean = false, userText: String) {
        if (userText.isBlank() && !isSystem) return
        if (isSystem) {
            send()
        } else {
            messages.add(
                ChatMessageUi(
                    userText.trim(),
                    isUser = true,
                    model = usingProModel.value.getModelName()
                )
            )
            getConversationHistory().forEach {
                it.history.add(
                    Message(
                        role = "user",
                        text = userText.trim()
                    )
                )
            }
            send()
        }
    }

    fun updateModel() {
        usingProModel.update { !it }
        if (getConversationHistory().size == 1) send()
    }

    private fun send() {
        isLoading.value = true
        lastError.value = null

        viewModelScope.launch {
            getConversationHistory().forEach { it ->
                delay(500)
                val result = repository.sendRequest(usingProModel.value, it)
                isLoading.value = false

                result.fold(onSuccess = { json ->
                    Log.i("OLOLO", "$json")
                    val assistantText =
                        extractFirstStringFromJson(json) ?: "[Не удалось получить текст ответа]"
                    // add assistant to UI and history
                    messages.add(
                        ChatMessageUi(
                            assistantText,
                            isUser = false,
                            model = it.name
                        )
                    )

                    it.history.add(
                        Message(
                            role = "assistant",
                            text = assistantText
                        )
                    )
                }, onFailure = { err ->
                    lastError.value = err.message ?: "Unknown error"
                    messages.add(
                        ChatMessageUi(
                            "Ошибка: ${err.message}",
                            isUser = false,
                            model = it.name
                        )
                    )
                })
            }
        }

    }

    // tries to find first string somewhere inside the Json response
    private fun extractFirstStringFromJson(json: JsonObject?): String? {
        if (json == null) return null
        return findFirstString(json)
    }

    private fun getConversationHistory() = if (usingProModel.value) agentProList else agentList

    private fun Boolean.getModelName() = if (this) "YandexGpt-pro" else "Экспертная группа"

    private fun findFirstString(element: JsonElement): String? {
        try {
            when {
                element.isJsonPrimitive && element.asJsonPrimitive.isString -> return element.asString
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    // try some common fields first
                    val commonKeys = listOf(
                        "result",
                        "choices",
                        "output",
                        "response",
                        "text",
                        "content",
                        "message"
                    )
                    for (k in commonKeys) {
                        if (obj.has(k)) {
                            val found = findFirstString(obj.get(k))
                            if (found != null) return found
                        }
                    }
                    // otherwise iterate entries
                    for ((_, v) in obj.entrySet()) {
                        val found = findFirstString(v)
                        if (found != null) return found
                    }
                }

                element.isJsonArray -> {
                    val arr = element.asJsonArray
                    for (el in arr) {
                        val found = findFirstString(el)
                        if (found != null) return found
                    }
                }
            }
        } catch (_: Exception) { /* ignore parsing exceptions and continue */
        }
        return null
    }
}