package com.example.superassistant.yandexgpt.presentation

import com.example.superassistant.yandexgpt.data.Message
import kotlin.collections.mutableListOf

data class Agent(
    val system: String,
    val history: MutableList<Message> = mutableListOf(SystemMessage(system).get()),
    val name: String,
    val temperature: Double
)
