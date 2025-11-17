import androidx.room.Entity
import androidx.room.PrimaryKey

// ---------------------- ROOT -----------------------------

@Entity(tableName = "request_dbo")
data class RequestDBO(
    @PrimaryKey()
    val id: Long,
    val completionOptions: CompletionOptionsDBO,
    val messages: List<MessageDBO>,
    val modelUri: String
)

// ------------------- COMPLETION OPTIONS -------------------

data class CompletionOptionsDBO(
    val maxTokens: String,
    val responseFormat: ResponseFormatDBO,
    val stream: Boolean,
    val temperature: Double
)

data class ResponseFormatDBO(
    val type: String
)

// ------------------------- MESSAGES ------------------------

data class MessageDBO(
    val role: String,
    val text: String
)
