package com.example.superassistant.ollama

import com.example.superassistant.ollama.models.Chunk
import kotlin.math.sqrt

class RagEngine(
    private val chunks: List<Chunk>,
    private val embedClient: suspend (String) -> List<Double>,
    private val llmClient: suspend (String) -> Unit
) {

    // Косинусная близость
    private fun cosineSimilarity(a: List<Double>, b: List<Double>): Double {
        val dot = a.zip(b).sumOf { (x, y) -> (x * y) }
        val normA = sqrt(a.sumOf { it * it })
        val normB = sqrt(b.sumOf { it * it })
        val similarity = dot / (normA * normB)
        return similarity
    }

    // Поиск топ-N релевантных чанков
    private fun searchRelevantChunks(
        queryEmbedding: List<Double>,
        topN: Int = 5
    ): List<Chunk> {
        return chunks
            .asSequence()
            .map { chunk ->
                val sim = cosineSimilarity(queryEmbedding, chunk.embedding)
                chunk to sim
            }
            .sortedByDescending { it.second }
            .take(topN)
            .map { it.first }
            .toList()
    }

    // Основная функция RAG
    suspend fun ask(question: String) {

        // 1) Эмбеддинг вопроса
        val queryEmbedding = embedClient(question)

        // 2) Поиск релевантных чанков
        val relevant = searchRelevantChunks(queryEmbedding)

        // 3) Формирование промпта
        val context = buildString {
            appendLine("Используй приведённый контекст для ответа. Если контекста недостаточно — скажи об этом.\n")
            appendLine("=== КОНТЕКСТ ===")
            relevant.forEachIndexed { index, c ->
                appendLine("[Чанк $index]: ${c.text}")
            }
            appendLine("\n=== ВОПРОС ===")
            appendLine(question)
        }

        // 4) Запрос к LLM
        llmClient(context)
    }
}
