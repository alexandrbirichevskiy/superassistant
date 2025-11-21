import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.*

class McpWebSocketClient {

    private val client = OkHttpClient()
    private var socket: WebSocket? = null
    val message = MutableStateFlow<String?>(null)

    fun connect() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/mcp")  // важно: 10.0.2.2 для Android-эмулятора
            .build()

        socket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("WS CONNECTED")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                message.update { (text) }
                println("MESSAGE: $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("CLOSING: $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("ERROR: ${t.message}")
            }
        })
    }

    fun send(text: String) {
        socket?.send(text.trimIndent())
    }
}
