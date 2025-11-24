package com.example.superassistant.ollama.dto

import com.google.gson.annotations.SerializedName

data class OllamaEmbeddingsRequestDTO(
    @SerializedName("model") val model: String = "nomic-embed-text",
    @SerializedName("prompt") val prompt: String
)

data class OllamaEmbeddingsResponseDTO(
    @SerializedName("embedding") val embedding: List<Double>
)