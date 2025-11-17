package com.example.superassistant.yandexgpt.data.network.dto

import com.google.gson.annotations.SerializedName

data class PromptRequestDTO(
    @SerializedName("modelUri") val modelUri: String,
    @SerializedName("completionOptions") val completionOptions: CompletionOptionsRequestDTO,
    @SerializedName("messages") val messages: List<MessageRequestDTO>
)

data class CompletionOptionsRequestDTO(
    @SerializedName("stream") val stream: Boolean,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("maxTokens") val maxTokens: String,
    @SerializedName("response_format") val responseFormat: TypeRequestDTO,
)

data class TypeRequestDTO(
    @SerializedName("type") val type: String
)

data class MessageRequestDTO(
    @SerializedName("role") val role: String,
    @SerializedName("text") val text: String,
)