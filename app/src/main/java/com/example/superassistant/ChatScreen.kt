package com.example.superassistant

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages = viewModel.messages
    val isLoading by viewModel.isLoading
    val lastError by remember { viewModel.lastError }

    val textState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(lastError) {
        lastError?.let { err ->
            scope.launch {
                snackbarHostState.showSnackbar(err)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Чат") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                reverseLayout = false
            ) {
                itemsIndexed(messages) { _, item ->
                    MessageRow(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введите сообщение...") },
                    singleLine = false,
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        val text = textState.value.trim()
                        if (text.isNotEmpty() && !isLoading) {
                            viewModel.sendUserMessage(false, text)
                            textState.value = ""
                        } else if (isLoading) {
                            Toast.makeText(
                                context,
                                "Пожалуйста подождите, ответ загружается...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageRow(message: ChatMessage) {

    var displayedText by remember { mutableStateOf(message.text) }
    var parsedData by remember { mutableStateOf(emptyList<CardData>()) }

    val bubbleColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFECECEC)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                displayedText = parseCardDataJson(message.text).toString()
                parsedData = parseCardDataJson(message.text)
            },
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(bubbleColor, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            if (parsedData.isEmpty()) {
                Text(text = displayedText, textAlign = TextAlign.Start)
            } else {
                Column {
                    for ( i in parsedData) {
                        i.title?.let { Text(text = it, textAlign = TextAlign.Start) }
                        i.description?.let { Text(text = it, textAlign = TextAlign.Start) }
                        i.shortDescription?.let { Text(text = it, textAlign = TextAlign.Start) }
                        i.keywords?.let { Text(text = it.toString(), textAlign = TextAlign.Start) }
                        Spacer(Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

fun parseCardDataJson(jsonString: String): List<CardData> {
    val gson = Gson()
    val listType = object : TypeToken<List<CardData>>() {}.type
    return gson.fromJson(jsonString, listType)
}
