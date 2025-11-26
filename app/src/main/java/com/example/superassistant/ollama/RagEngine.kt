package com.example.superassistant.ollama

import com.example.superassistant.ollama.models.Chunk
import kotlin.math.sqrt

class RagEngine(
    private val chunks: List<Chunk>,
    private val embedClient: suspend (String) -> List<Double>,
    private val llmClient: suspend (String) -> Unit,
    private val reranker: suspend (String, List<Pair<Chunk, Double>>) -> List<Chunk>
) {

    private fun cosineSimilarity(a: List<Double>, b: List<Double>): Double {
        val dot = a.zip(b).sumOf { (x, y) -> (x * y) }
        val normA = sqrt(a.sumOf { (it * it) })
        val normB = sqrt(b.sumOf { (it * it) })
        return dot / (normA * normB)
    }

    private fun initialSearch(
        emb: List<Double>,
        topN: Int = 10
    ): List<Pair<Chunk, Double>> {
        return chunks.map { chunk ->
            chunk to cosineSimilarity(emb, chunk.embedding)
        }
            .sortedByDescending { it.second }
            .take(topN)
    }

    suspend fun ask(question: String) {
        val qEmbedding = embedClient(question)

        // Этап 1. Поиск
        val candidates = initialSearch(qEmbedding)

        // Этап 2. Reranker / фильтр
        val filtered = reranker(question, candidates)

        // Этап 3. Формирование промпта
        val context = buildString {
            appendLine("Используй только приведённый контекст.")
            appendLine("=== КОНТЕКСТ ===")
            filtered.forEachIndexed { index, c ->
                appendLine("[Чанк $index]: ${c.text}")
            }
            appendLine("\n=== ВОПРОС ===\n$question")
        }

        // Этап 4. Ответ LLM
        llmClient(context)
    }
}

