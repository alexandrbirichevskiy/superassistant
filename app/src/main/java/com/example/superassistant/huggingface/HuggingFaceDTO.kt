package com.example.superassistant.huggingface

import com.google.gson.annotations.SerializedName

data class ChatRequestDTO(
    @SerializedName("messages") val messages: List<MessageDTO>,
    @SerializedName("model") val model: String
)

data class MessageDTO(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ChatResponseDTO(
    @SerializedName("choices") val choices: List<ChoiceDTO>
)

data class ChoiceDTO(
    @SerializedName("message") val message: MessageDTO
)
