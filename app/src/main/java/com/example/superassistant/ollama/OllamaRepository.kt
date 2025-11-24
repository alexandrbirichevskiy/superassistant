package com.example.superassistant.ollama

import android.util.Log
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.ollama.dto.OllamaEmbeddingsRequestDTO
import com.example.superassistant.ollama.models.ChunkEmbedding
import com.example.superassistant.ollama.models.EmbeddingIndex
import com.example.superassistant.ollama.network.OllamaApi
import com.google.gson.GsonBuilder
import org.json.JSONObject
import org.json.JSONArray
import java.io.File

class OllamaRepository(private val retrofit: SuperAssistantRetrofit) {

    private val api by lazy {
        retrofit.createApi(
            "",
            "http://10.0.2.2:11434",
            OllamaApi::class.java
        )
    }


    suspend fun sendRequest(text: String): List<Double> {
        val response = api.embeddings(request = OllamaEmbeddingsRequestDTO(prompt = text))
        val embedding = response.body()?.embedding

        return if (!embedding.isNullOrEmpty()) {
            embedding
        } else {
            emptyList()
        }
    }

    fun chunkText(
        text: String,
        chunkSize: Int = 800,
        overlap: Int = 200
    ): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0

        while (start < text.length) {
            val end = (start + chunkSize).coerceAtMost(text.length)
            val chunk = text.substring(start, end).trim()
            chunks.add(chunk)

            start += (chunkSize - overlap)
        }

        return chunks
    }

    suspend fun processFiles(files: List<Pair<String, String>>, outputJson: String) {
        val allChunks = mutableListOf<ChunkEmbedding>()

        files.forEach { text ->

            val chunks = chunkText(text = text.second, chunkSize = 800, overlap = 200)

            chunks.forEachIndexed { index, chunkText ->
                Log.e("OLOLO","Embedding ${text.first} [chunk $index/${chunks.size}]")

                val emb = sendRequest(chunkText)

                allChunks.add(
                    ChunkEmbedding(
                        fileName = text.first,
                        chunkId = index,
                        text = chunkText,
                        embedding = emb
                    )
                )
            }
        }

        val index = EmbeddingIndex(chunks = allChunks)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(index)

        File(outputJson).writeText(jsonString, Charsets.UTF_8)

        Log.e("OLOLO", "Saved index to: $outputJson")
    }

}