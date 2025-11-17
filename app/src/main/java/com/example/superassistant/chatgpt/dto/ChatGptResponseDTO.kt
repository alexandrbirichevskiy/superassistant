package com.example.superassistant.chatgpt.dto

import com.google.gson.annotations.SerializedName

data class ChatGptResponseDTO (
    @SerializedName("id") val id: String,
    @SerializedName("output") val output: List<ChatGptOutputDTO>
)

data class ChatGptOutputDTO(
    @SerializedName("content") val content: List<ChatGptContentDTO>
)

data class ChatGptContentDTO(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)
