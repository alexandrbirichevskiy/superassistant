package com.example.superassistant.yandexgpt.presentation

data class ChatMessageUi(
    val text: String,
    val isUser: Boolean,
    val model: String,
    val tokens: String?,
    val maxTokens: String,
)