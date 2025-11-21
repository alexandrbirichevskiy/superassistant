package com.example.superassistant.chatgpt.dto

import com.google.gson.annotations.SerializedName

data class ChatGptResponseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("object") val type: String?,
    @SerializedName("created") val created: Long?,
    @SerializedName("model") val model: String?,
    @SerializedName("choices") val choices: List<ChatGptChoiceDto>?,
    @SerializedName("usage") val usage: ChatGptUsageDto?
)

data class ChatGptChoiceDto(
    @SerializedName("index") val index: Int?,
    @SerializedName("message") val message: MessageGPTResponseDTI?,
    @SerializedName("finish_reason") val finishReason: String?
)

data class ChatGptUsageDto(
    @SerializedName("prompt_tokens") val promptTokens: Int?,
    @SerializedName("completion_tokens") val completionTokens: Int?,
    @SerializedName("total_tokens") val totalTokens: Int?
)

data class MessageGPTResponseDTI(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String?,
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>?,
    @SerializedName("refusal") val refusal: Any?,           // в ответе null
    @SerializedName("annotations") val annotations: List<Any>   // пустой список []
)

data class ToolCall(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("function") val function: ToolFunctionResponse
)

data class ToolFunctionResponse(
    @SerializedName("name") val name: String,
    @SerializedName("arguments") val arguments: String        // приходит как строка JSON
)