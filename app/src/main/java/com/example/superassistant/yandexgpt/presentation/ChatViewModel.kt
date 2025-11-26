package com.example.superassistant.yandexgpt.presentation

import RequestDBO
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superassistant.Keys
import com.example.superassistant.chatgpt.ChatGptRepository
import com.example.superassistant.ollama.OllamaRepository
import com.example.superassistant.ollama.models.Chunk
import com.example.superassistant.yandexgpt.data.ChatRepository
import com.example.superassistant.yandexgpt.data.network.dto.MessageRequestDTO
import com.example.superassistant.yandexgpt.presentation.models.Agent
import com.example.superassistant.yandexgpt.presentation.models.ChatMessageUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(
    private val dialog: Dialog,
    private val repository: ChatRepository,
    private val chatGptRepository: ChatGptRepository,
    private val ollamaRepository: OllamaRepository
) : ViewModel() {

    val name = dialog.name
    val messages = mutableStateListOf<ChatMessageUi>()
    var isLoading = mutableStateOf(false)
    var lastError = mutableStateOf<String?>(null)

    val agent = Agent(
        system = Keys.SYSTEM,
        name = dialog.model,
        temperature = 0.2,
        maxTokens = "1000"
    )


    private val agentList = listOf(agent)

    init {
        sendUserMessage(isSystem = true, userText = "", true)
    }

    fun processFiles(files: List<Pair<String, String>>) {
        viewModelScope.launch(Dispatchers.IO) {
            ollamaRepository.processFiles(
                files = files
            )
        }
    }

    fun connect() {
        viewModelScope.launch {
            repository.connect()
        }
    }

    fun restore(data: RequestDBO) {
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

    val thresholdReranker: suspend (String, List<Pair<Chunk, Double>>) -> List<Chunk> =
        { _, list ->
            list.filter {
                if (dialog.name == "Без фильтра") it.second >= 0 else it.second >= 0.736
            }.map {
                Log.e("OLOLO", "${it.first.id}: ${it.second}")
                it.first
            }
        }


    fun ask(userText: String) {
        viewModelScope.launch {
            val rag = ollamaRepository.getRag(
                llmClient = { sendUserMessage(false, it, true) },
                reranker = thresholdReranker
            )

            rag.ask(userText)
        }
    }

    fun sendUserMessage(isSystem: Boolean = false, userText: String, isShow: Boolean) {
        if (userText.isBlank() && !isSystem) return
        if (isSystem) {
            send()
        } else {
            if (isShow) {
                messages.add(
                    ChatMessageUi(
                        userText.trim(),
                        isUser = true,
                    )
                )
            }

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

                val result = chatGptRepository.sendRequest(agent)
                isLoading.value = false

                result.fold(onSuccess = { model ->
                    val text = model.choices?.first()?.message?.content
                    val a = model.choices
                        ?.first()
                        ?.message
                        ?.toolCalls
                        ?.first()

                    if (!(a?.function?.arguments.isNullOrEmpty()) && text.isNullOrEmpty()) {
                        repository.send(
                            text = a.function.arguments,
                            name = a.function.name,
                        )
                    }

                    if (text != null) {
                        messages.add(
                            ChatMessageUi(
                                text,
                                isUser = false,
                            )
                        )

                        it.history.add(
                            MessageRequestDTO(
                                role = "assistant",
                                text = text
                            )
                        )
                    }
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