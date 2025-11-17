package com.example.superassistant.yandexgpt.data.network.dto

import com.google.gson.annotations.SerializedName

data class RootResponseDTO(
    @SerializedName("result")
    val result: ResultResponseDTO
)

data class ResultResponseDTO(
    @SerializedName("alternatives")
    val alternatives: List<AlternativeResponseDTO>,

    @SerializedName("usage")
    val usage: UsageResponseDTO,

    @SerializedName("modelVersion")
    val modelVersion: String
)

data class AlternativeResponseDTO(
    @SerializedName("message")
    val message: MessageResponseDTO,

    @SerializedName("status")
    val status: String
)

data class MessageResponseDTO(
    @SerializedName("role")
    val role: String,

    @SerializedName("text")
    val text: String
)

data class UsageResponseDTO(
    @SerializedName("inputTextTokens")
    val inputTextTokens: String,

    @SerializedName("completionTokens")
    val completionTokens: String,

    @SerializedName("totalTokens")
    val totalTokens: String,

    @SerializedName("completionTokensDetails")
    val completionTokensDetails: CompletionTokensDetailsResponseDTO
)

data class CompletionTokensDetailsResponseDTO(
    @SerializedName("reasoningTokens")
    val reasoningTokens: String
)
