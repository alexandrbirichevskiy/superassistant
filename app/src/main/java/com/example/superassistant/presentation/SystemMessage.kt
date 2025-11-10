package com.example.superassistant.presentation

import com.example.superassistant.data.Message

class SystemMessage(private val request: String) {

    fun get() = Message(
        role = "system",
        text = request
    )
}