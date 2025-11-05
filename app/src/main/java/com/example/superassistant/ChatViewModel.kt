package com.example.superassistant

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class ChatViewModel() : ViewModel() {

    val messages = mutableStateListOf<ChatMessage>()
    var isLoading = androidx.compose.runtime.mutableStateOf(false)
    var lastError = androidx.compose.runtime.mutableStateOf<String?>(null)

    // initial system context
    private val systemMessage = Message(role = "system", text = "Ты специалист по Формуле 1, коротко расскажи о себе будто бы ты консультант")
    private val conversationHistory = mutableListOf(systemMessage)

    private val retrofit = SuperAssistantRetrofit()
    private val repository = ChatRepository(retrofit)

    fun sendUserMessage(isSystem: Boolean = false, userText: String) {
        if (userText.isBlank() && !isSystem) return
        if (isSystem) {
            send()
        } else {
            messages.add(ChatMessage(userText.trim(), isUser = true))
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
                val assistantText = extractFirstStringFromJson(json) ?: "[Не удалось получить текст ответа]"
                // add assistant to UI and history
                messages.add(ChatMessage(assistantText, isUser = false))
                conversationHistory.add(Message(role = "assistant", text = assistantText))
            }, onFailure = { err ->
                lastError.value = err.message ?: "Unknown error"
                messages.add(ChatMessage("Ошибка: ${err.message}", isUser = false))
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
                    val commonKeys = listOf("result", "choices", "output", "response", "text", "content", "message")
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
        } catch (_: Exception) { /* ignore parsing exceptions and continue */ }
        return null
    }
}