package com.example.superassistant.yandexgpt.presentation

data class ChatMessageUi(
    val text: String,
    val isUser: Boolean,
    val model: String,
    val time: String,
    val tokens: String?
)