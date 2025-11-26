package com.example.superassistant.ollama

import android.content.Context
import android.util.Log
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.ollama.dto.OllamaEmbeddingsRequestDTO
import com.example.superassistant.ollama.models.Chunk
import com.example.superassistant.ollama.models.ChunkEmbedding
import com.example.superassistant.ollama.models.EmbeddingIndex
import com.example.superassistant.ollama.network.OllamaApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class OllamaRepository(
    private val retrofit: SuperAssistantRetrofit,
    private val appContext: Context
) {

    private val api by lazy {
        retrofit.createApi(
            "",
            "http://10.0.2.2:11434",
            OllamaApi::class.java
        )
    }

    fun getRag(
        llmClient: suspend (String) -> Unit,
        reranker: suspend (String, List<Pair<Chunk, Double>>) -> List<Chunk>
    ): RagEngine {
        return RagEngine(
            chunks = readJson(appContext, "lol.json"),
            embedClient = { getEmbeddingQuestion(it) },
            llmClient = llmClient,
            reranker = reranker
        )
    }

    suspend fun getEmbeddingQuestion(text: String): List<Double> {
        val emb = sendRequest(text)
        return emb
    }

    suspend fun processFiles(files: List<Pair<String, String>>) {
        val allChunks = mutableListOf<ChunkEmbedding>()

        files.forEach { text ->

            val chunks = chunkText(text = text.second, chunkSize = 500, overlap = 150)

            chunks.forEachIndexed { index, chunkText ->

                Log.e("OLOLO", "Embedding ${text.first} [chunk $index/${chunks.size}]")
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
        saveJson(appContext, "lol.json", jsonString)
    }


    private suspend fun sendRequest(text: String): List<Double> {
        val response = api.embeddings(request = OllamaEmbeddingsRequestDTO(prompt = text))
        val embedding = response.body()?.embedding

        return if (!embedding.isNullOrEmpty()) {
            embedding
        } else {
            emptyList()
        }
    }

    fun saveJson(context: Context, fileName: String, json: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(json.toByteArray())
        }
    }

    fun readJson(context: Context, fileName: String): List<Chunk> {
        val json = context.openFileInput(fileName)
            .bufferedReader()
            .use { it.readText() }
        val ebm = Gson().fromJson(json, EmbeddingIndex::class.java).chunks
        return ebm.map {
            Chunk(
                id = it.chunkId,
                text = it.text,
                embedding = it.embedding
            )
        }
    }


    private fun chunkText(
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
}
