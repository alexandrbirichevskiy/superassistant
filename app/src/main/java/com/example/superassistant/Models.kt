package com.example.superassistant

import com.google.gson.annotations.SerializedName

data class Prompt(
    @SerializedName("modelUri") val modelUri: String,
    @SerializedName("completionOptions") val completionOptions: CompletionOptions,
    @SerializedName("messages") val messages: List<Message>
)

data class CompletionOptions(
    @SerializedName("stream") val stream: Boolean,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("maxTokens") val maxTokens: String,
    @SerializedName("response_format") val responseFormat: Type,
)

data class Type(
    @SerializedName("type") val type: String
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("text") val text: String
)