package com.example.superassistant.ollama.models

data class Chunk(
    val id: Int,
    val text: String,
    val embedding: List<Double>
)
