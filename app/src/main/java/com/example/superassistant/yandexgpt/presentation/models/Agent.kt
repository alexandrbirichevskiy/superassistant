package com.example.superassistant.yandexgpt.presentation.models

import com.example.superassistant.yandexgpt.data.network.dto.MessageRequestDTO
import com.example.superassistant.yandexgpt.presentation.SystemMessage

data class Agent(
    val system: String,
    val history: MutableList<MessageRequestDTO> = mutableListOf(SystemMessage(system).get()),
    val name: String,
    val temperature: Double,
    val maxTokens: String,
)
