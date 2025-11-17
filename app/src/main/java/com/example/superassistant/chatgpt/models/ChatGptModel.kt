package com.example.superassistant.chatgpt.models

data class ChatGptModel(
    val id: String,
    val type: String,
    val created: Long,
    val ownedBy: String,
)
