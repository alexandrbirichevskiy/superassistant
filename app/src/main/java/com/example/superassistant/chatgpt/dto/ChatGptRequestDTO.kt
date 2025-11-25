package com.example.superassistant.chatgpt.dto

import com.google.gson.annotations.SerializedName

data class ChatGptRequestDto(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatGptMessageDto>,
    @SerializedName("temperature") val temperature: Double? = null,
//    @SerializedName("max_tokens") val maxTokens: Int? = null,
    @SerializedName("stream") val stream: Boolean? = null,
//    @SerializedName("tools") val tools: List<Tool>
)

data class ChatGptMessageDto(
    @SerializedName("role") val role: String,   // "user" | "assistant" | "system"
    @SerializedName("content") val content: String
)

data class Tool(
    @SerializedName("type") val type: String = "function",
    @SerializedName("function") val function: ToolFunction
)

data class ToolFunction(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("parameters") val parameters: Map<String, Any>? = null
)
