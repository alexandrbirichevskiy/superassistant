package com.example.superassistant.yandexgpt.presentation

import RequestDBO
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.yandexgpt.data.ChatRepository
import com.example.superassistant.yandexgpt.data.network.dto.MessageRequestDTO
import com.example.superassistant.yandexgpt.presentation.models.Agent
import com.example.superassistant.yandexgpt.presentation.models.ChatMessageUi
import kotlinx.coroutines.launch

class ChatViewModel(
    private val dialog: Dialog,
    private val repository: ChatRepository
) : ViewModel() {

    val name = dialog.name
    val messages = mutableStateListOf<ChatMessageUi>()
    var isLoading = mutableStateOf(false)
    var lastError = mutableStateOf<String?>(null)

    val agent = Agent(
        system = "Ты - рыба Немо из мультика в поисках Немо",
        name = dialog.model,
        temperature = 0.5,
        maxTokens = "1000"
    )

    private val agentList = listOf(agent)


    init {
        viewModelScope.launch {
            val restoredData = repository.getChat(dialog.id.toLong())
            if (restoredData == null) {
                sendUserMessage(true, "")
            } else {
                restore(restoredData)
            }
        }
    }

    fun restore(data:  RequestDBO) {
        data.messages.forEach {
            if (it.role != "system") {
                messages.add(
                    ChatMessageUi(
                        it.text,
                        isUser = it.role == "user"
                    )
                )
            }
        }

        agentList.forEach { agent ->
            data.messages.forEach { mes ->
                agent.history.add(
                    MessageRequestDTO(
                        role = mes.role,
                        text = mes.text
                    )
                )
            }
        }
    }

    fun saveChat() {
        viewModelScope.launch {
            repository.saveRequest(dialog)
        }
    }

    fun sendUserMessage(isSystem: Boolean = false, userText: String) {
        if (userText.isBlank() && !isSystem) return
        if (isSystem) {
            send()
        } else {
            messages.add(
                ChatMessageUi(
                    userText.trim(),
                    isUser = true,
                )
            )
            agentList.forEach {
                it.history.add(
                    MessageRequestDTO(
                        role = "user",
                        text = userText.trim()
                    )
                )
            }
            send()
        }
    }

    private fun send() {
        isLoading.value = true
        lastError.value = null

        viewModelScope.launch {
            agentList.forEach { it ->

                val result = repository.sendRequest(it)
                isLoading.value = false

                result.fold(onSuccess = { model ->

                    messages.add(
                        ChatMessageUi(
                            model.result.alternatives.first().message.text,
                            isUser = false,
                        )
                    )

                    it.history.add(
                        MessageRequestDTO(
                            role = "assistant",
                            text = model.result.alternatives.first().message.text
                        )
                    )
                }, onFailure = { err ->
                    lastError.value = err.message ?: "Unknown error"
                    messages.add(
                        ChatMessageUi(
                            "Ошибка: ${err.message}",
                            isUser = false,
                        )
                    )
                })
            }
        }

    }
}