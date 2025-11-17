package com.example.superassistant.chatgpt.dto

import com.google.gson.annotations.SerializedName

data class ChatGptModelsDTO(
    @SerializedName("data") val data: List<ChatGptModelDTO>
)

data class ChatGptModelDTO(
    @SerializedName("id") val id: String,
    @SerializedName("object") val type: String,
    @SerializedName("created") val created: Long,
    @SerializedName("owned_by") val ownedBy: String,
)
