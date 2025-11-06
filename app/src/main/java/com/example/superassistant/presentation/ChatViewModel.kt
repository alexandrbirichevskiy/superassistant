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
import kotlinx.coroutines.launch

class ChatViewModel() : ViewModel() {

    val messages = mutableStateListOf<ChatMessageUi>()
    var isLoading = mutableStateOf(false)
    var lastError = mutableStateOf<String?>(null)
    private val systemMessage = SystemMessage.get()
    private val conversationHistory = mutableListOf(systemMessage)

    private val retrofit = SuperAssistantRetrofit()
    private val repository = ChatRepository(retrofit)

    fun sendUserMessage(isSystem: Boolean = false, userText: String) {
        if (userText.isBlank() && !isSystem) return
        if (isSystem) {
            send()
        } else {
            messages.add(ChatMessageUi(userText.trim(), isUser = true))
            conversationHistory.add(Message(role = "user", text = userText.trim()))
            send()
        }
    }

    private fun send() {
        isLoading.value = true
        lastError.value = null

        viewModelScope.launch {
            val result = repository.sendRequest(conversationHistory)
            isLoading.value = false

            result.fold(onSuccess = { json ->
                Log.i("OLOLO", "$json")
                val assistantText =
                    extractFirstStringFromJson(json) ?: "[Не удалось получить текст ответа]"
                // add assistant to UI and history
                if (messages.isEmpty()) {
                    messages.add(ChatMessageUi("Привет! Чем могу быть полезен?", isUser = false))
                } else {
                    messages.add(ChatMessageUi(assistantText, isUser = false))
                }
                conversationHistory.add(Message(role = "assistant", text = assistantText))
            }, onFailure = { err ->
                lastError.value = err.message ?: "Unknown error"
                messages.add(ChatMessageUi("Ошибка: ${err.message}", isUser = false))
            })
        }
    }

    // tries to find first string somewhere inside the Json response
    private fun extractFirstStringFromJson(json: JsonObject?): String? {
        if (json == null) return null
        return findFirstString(json)
    }

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