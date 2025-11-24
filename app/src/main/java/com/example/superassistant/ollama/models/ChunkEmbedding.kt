package com.example.superassistant.ollama.models

data class ChunkEmbedding(
    val fileName: String,
    val chunkId: Int,
    val text: String,
    val embedding: List<Double>
)

data class EmbeddingIndex(
    val chunks: List<ChunkEmbedding>
)
