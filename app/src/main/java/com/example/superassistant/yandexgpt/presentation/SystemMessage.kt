package com.example.superassistant.yandexgpt.presentation

import com.example.superassistant.yandexgpt.data.network.dto.MessageRequestDTO

class SystemMessage(private val request: String) {

    fun get() = MessageRequestDTO(
        role = "system",
        text = request
    )
}