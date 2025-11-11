package com.example.superassistant.yandexgpt.presentation

import com.example.superassistant.yandexgpt.data.Message

class SystemMessage(private val request: String) {

    fun get() = Message(
        role = "system",
        text = request
    )
}