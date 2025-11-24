package com.example.superassistant.ollama.network

import com.example.superassistant.ollama.dto.OllamaEmbeddingsRequestDTO
import com.example.superassistant.ollama.dto.OllamaEmbeddingsResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OllamaApi {
    @POST("api/embeddings")
    suspend fun embeddings(@Body request: OllamaEmbeddingsRequestDTO): Response<OllamaEmbeddingsResponseDTO>
}