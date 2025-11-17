package com.example.superassistant.chatgpt.dto

import com.google.gson.annotations.SerializedName

data class ChatGptRequestDTO(
    @SerializedName("model") val model: String,
    @SerializedName("input") val input: String
)
